function currencyToNumber(text) {
  if (!text) return 0;
  return Number(String(text).replace(/[^\d.-]/g, ''));
}

module.exports = { currencyToNumber };
