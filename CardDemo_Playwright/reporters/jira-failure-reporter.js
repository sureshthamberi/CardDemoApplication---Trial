const { attachJiraFiles, createJiraIssue, getJiraConfig } = require('../utils/jira');

class JiraFailureReporter {
  constructor() {
    this.resultsByTestId = new Map();
  }

  onTestEnd(test, result) {
    this.resultsByTestId.set(test.id, { test, result });
  }

  formatStepContent(steps, indent = '') {
    return (steps || []).flatMap((step) => {
      const failed = Boolean(step.error);
      const marks = failed
        ? [{ type: 'strong' }, { type: 'textColor', attrs: { color: '#DE350B' } }]
        : [];
      const content = [{ type: 'text', text: `${indent}- ${step.title}`, marks }];
      return [
        { type: 'paragraph', content },
        ...this.formatStepContent(step.steps, `${indent}  `)
      ];
    });
  }

  async onEnd() {
    const config = getJiraConfig();
    const failures = [...this.resultsByTestId.values()]
      .filter(({ result }) => ['failed', 'timedOut', 'interrupted'].includes(result.status));

    if (!config.enabled || failures.length === 0) {
      return;
    }

    for (const { test, result } of failures) {
      const errorText = result.errors
        .map((error) => error.message || error.value || 'Unknown test failure')
        .join('\n\n');
      const summary = `[Playwright] ${test.title}`.slice(0, 255);
      const failureScreenshots = (result.attachments || [])
        .filter((attachment) => attachment.path && attachment.contentType?.startsWith('image/'));
      const stepContent = this.formatStepContent(result.steps);
      const descriptionDocument = {
        type: 'doc',
        version: 1,
        content: [
          { type: 'heading', attrs: { level: 3 }, content: [{ type: 'text', text: 'Test steps' }] },
          ...(stepContent.length ? stepContent : [{ type: 'paragraph', content: [{ type: 'text', text: '- No test steps were captured.' }] }]),
          { type: 'heading', attrs: { level: 3 }, content: [{ type: 'text', text: 'Error details' }] },
          { type: 'paragraph', content: [{ type: 'text', text: errorText || 'No error details were provided.' }] },
          { type: 'heading', attrs: { level: 3 }, content: [{ type: 'text', text: 'Failure screenshot' }] },
          { type: 'paragraph', content: [{ type: 'text', text: failureScreenshots.length ? 'Attached to this Jira ticket.' : 'No failure screenshot was captured.' }] }
        ]
      };

      try {
        const issue = await createJiraIssue({ summary, descriptionDocument });
        const uploadedFiles = await attachJiraFiles(issue.key, failureScreenshots);
        if (uploadedFiles.length) {
          console.log(`Jira attachments uploaded: ${uploadedFiles.join(', ')}`);
        }
        console.log(`Jira issue created: ${issue.key || 'unknown key'}`);
      } catch (error) {
        console.error(`Unable to create Jira issue for failed test "${test.title}": ${error.message}`);
      }
    }
  }
}

module.exports = JiraFailureReporter;