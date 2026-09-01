const DEFAULT_JIRA_BASE_URL = 'https://ibm-example-sandbox001.atlassian.net';
const ASSIGNEE_PLACEHOLDER = 'REPLACE_WITH_REQUIRED_ASSIGNEE_ACCOUNT_ID';
const fs = require('fs');
const path = require('path');

function getJiraConfig(env = process.env) {
  return {
    enabled: env.JIRA_CREATE_ISSUES === 'true',
    baseUrl: (env.JIRA_BASE_URL || DEFAULT_JIRA_BASE_URL).replace(/\/$/, ''),
    projectKey: env.JIRA_PROJECT_KEY || 'DLAC',
    issueType: env.JIRA_ISSUE_TYPE || 'Bug',
    assignee: env.JIRA_ASSIGNEE_ACCOUNT_ID || env.JIRA_ASSIGNEE_EMAIL || env.JIRA_EMAIL,
    email: env.JIRA_EMAIL,
    apiToken: env.JIRA_API_TOKEN
  };
}

function isConfigured(config) {
  return config.enabled &&
    Boolean(config.email && config.apiToken) &&
    Boolean(config.assignee) &&
    config.assignee !== ASSIGNEE_PLACEHOLDER;
}

function getAuthHeaders(config) {
  return {
    Authorization: `Basic ${Buffer.from(`${config.email}:${config.apiToken}`).toString('base64')}`,
    'Content-Type': 'application/json',
    Accept: 'application/json'
  };
}

async function resolveAssigneeAccountId(config) {
  if (!config.assignee.includes('@')) {
    return config.assignee;
  }

  const response = await fetch(
    `${config.baseUrl}/rest/api/3/user/search?query=${encodeURIComponent(config.assignee)}`,
    { headers: getAuthHeaders(config) }
  );

  if (!response.ok) {
    throw new Error(`Jira assignee lookup failed (${response.status}): ${await response.text()}`);
  }

  const users = await response.json();
  const assignee = users.find((user) => user.emailAddress === config.assignee) || users[0];
  if (!assignee?.accountId) {
    throw new Error(`No assignable Jira user found for ${config.assignee}`);
  }

  return assignee.accountId;
}

async function verifyProject(config) {
  const response = await fetch(
    `${config.baseUrl}/rest/api/3/project/${encodeURIComponent(config.projectKey)}`,
    { headers: getAuthHeaders(config) }
  );

  if (!response.ok) {
    throw new Error(
      `Jira project "${config.projectKey}" was not found or is not accessible at ${config.baseUrl}. ` +
      'Set JIRA_BASE_URL and JIRA_PROJECT_KEY to a project visible to this Jira account.'
    );
  }
}

async function createJiraIssue(issue, env = process.env) {
  const config = getJiraConfig(env);

  if (!config.enabled) {
    return { created: false, reason: 'JIRA_CREATE_ISSUES is not true' };
  }

  if (!isConfigured(config)) {
    throw new Error(
      'Jira is enabled but JIRA_EMAIL, JIRA_API_TOKEN, and JIRA_ASSIGNEE_EMAIL or JIRA_ASSIGNEE_ACCOUNT_ID must be configured'
    );
  }

  await verifyProject(config);
  const assigneeAccountId = await resolveAssigneeAccountId(config);
  const response = await fetch(`${config.baseUrl}/rest/api/3/issue`, {
    method: 'POST',
    headers: getAuthHeaders(config),
    body: JSON.stringify({
      fields: {
        project: { key: config.projectKey },
        summary: issue.summary,
        description: issue.descriptionDocument || {
          type: 'doc',
          version: 1,
          content: [{
            type: 'paragraph',
            content: [{ type: 'text', text: issue.description }]
          }]
        },
        issuetype: { name: config.issueType },
        assignee: { accountId: assigneeAccountId },
        labels: ['playwright', 'automated-test-failure']
      }
    })
  });

  if (!response.ok) {
    const responseBody = await response.text();
    throw new Error(`Jira issue creation failed (${response.status}): ${responseBody}`);
  }

  return { created: true, ...(await response.json()) };
}

async function attachJiraFiles(issueKey, attachments, env = process.env) {
  const config = getJiraConfig(env);

  if (!config.enabled || !issueKey || !attachments?.length) {
    return [];
  }

  if (!isConfigured(config)) {
    throw new Error(
      'Jira is enabled but JIRA_EMAIL, JIRA_API_TOKEN, and JIRA_ASSIGNEE_EMAIL or JIRA_ASSIGNEE_ACCOUNT_ID must be configured'
    );
  }

  const uploadedFiles = [];
  for (const attachment of attachments) {
    if (!attachment.path || !fs.existsSync(attachment.path)) {
      continue;
    }

    const form = new FormData();
    form.append(
      'file',
      new Blob([fs.readFileSync(attachment.path)], { type: attachment.contentType || 'application/octet-stream' }),
      path.basename(attachment.path)
    );

    const response = await fetch(`${config.baseUrl}/rest/api/3/issue/${encodeURIComponent(issueKey)}/attachments`, {
      method: 'POST',
      headers: {
        Authorization: getAuthHeaders(config).Authorization,
        'X-Atlassian-Token': 'no-check'
      },
      body: form
    });

    if (!response.ok) {
      throw new Error(`Jira attachment upload failed (${response.status}): ${await response.text()}`);
    }

    uploadedFiles.push(path.basename(attachment.path));
  }

  return uploadedFiles;
}

module.exports = {
  ASSIGNEE_PLACEHOLDER,
  attachJiraFiles,
  createJiraIssue,
  getJiraConfig
};