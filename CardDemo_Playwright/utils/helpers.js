function parseCurrency(value) {
  if (!value) return 0;
  return Number(String(value).replace(/[^0-9.-]/g, ''));
}

function addAmounts(...amounts) {
  return amounts.reduce((sum, value) => sum + Number(value || 0), 0);
}

function formatDateInput(dateObj) {
  return `${dateObj.day}/${dateObj.month}/${dateObj.year}`;
}

module.exports = {
  parseCurrency,
  addAmounts,
  formatDateInput
};
