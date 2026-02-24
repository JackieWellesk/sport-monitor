// src/config/menu.js
export const menuTree = [
    {
        key: 'dashboard',
        titleKey: 'menu.dashboard',
        icon: 'HomeFilled',
        path: '/home',
        roles: ['STUDENT']
    },
    {
        key: 'user_profile',
        titleKey: 'menu.user.profile',
        icon: 'User',
        path: '/users/profile',
        roles: ['STUDENT']
    },
    {
        key: 'user_record',
        titleKey: 'menu.record.root',
        icon: 'Timer',
        path: '/records/list',
        roles: ['STUDENT']
    },
    {
        key: 'user_signup',
        titleKey: 'menu.event.signup',
        icon: 'EditPen',
        path: '/events/signups',
        roles: ['STUDENT']
    },
    {
        key: 'user_event_result',
        titleKey: 'menu.user.result',
        icon: 'Trophy',
        path: '/events/results',
        roles: ['STUDENT']
    },
    {
        key: 'HealthReport',
        titleKey: 'menu.report.health',
        icon: 'DataAnalysis',
        path: '/health-report',
        roles: ['STUDENT']
    }

]
export function filterMenuByRole(menu, role) {
    return menu
        .filter(item => {
            if (!item.roles) return true
            return item.roles.includes(role)
        })
        .map(item => {
            const newItem = { ...item }

            if (newItem.children) {
                newItem.children = filterMenuByRole(newItem.children, role)
            }

            return newItem
        })
}
