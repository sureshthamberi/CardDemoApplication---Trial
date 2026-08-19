const axios = require('axios');

const API_BASE = process.env.API_BASE_URL || 'http://localhost:8080/api/v1';

/**
 * Create an Axios instance for a given request, injecting the session JWT.
 * @param {object} req - Express request (for reading session token)
 * @returns {import('axios').AxiosInstance}
 */
function apiClient(req) {
    const token = req && req.session && req.session.token;
    return axios.create({
        baseURL: API_BASE,
        timeout: 10000,
        headers: {
            'Content-Type':  'application/json',
            'Accept':        'application/json',
            ...(token ? { Authorization: `Bearer ${token}` } : {})
        }
    });
}

module.exports = { apiClient, API_BASE };
