/**
 * Authentication middleware — redirect to login if no session user.
 */
function requireAuth(req, res, next) {
    if (!req.session || !req.session.user) {
        return res.redirect('/auth/login');
    }
    next();
}

/**
 * Role guard middleware factory.
 * @param {'ADMIN' | 'STANDARD'} role
 */
function requireRole(role) {
    return (req, res, next) => {
        if (!req.session || !req.session.user) {
            return res.redirect('/auth/login');
        }
        if (req.session.user.userType !== role) {
            return res.status(403).render('error.njk', {
                title: 'Access denied',
                message: 'You do not have permission to access this page.',
                statusCode: 403
            });
        }
        next();
    };
}

module.exports = { requireAuth, requireRole };
