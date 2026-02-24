// src/config/menu.js
export const menuTree = [
    {
        key: 'dashboard',
        titleKey: 'menu.dashboard',
        icon: 'HomeFilled',
        path: '/home',
        roles: ['ADMIN', 'TEACHER']
    },
    {
        key: 'user',
        titleKey: 'menu.user.root',
        icon: 'UserFilled',
        path: '/users',
        roles: ['ADMIN', 'TEACHER'],
        children: [
            {
                key: 'user_list',
                titleKey: 'menu.user.list',
                icon: 'User',
                path: '/users/list',
                roles: ['ADMIN']
            },
            {
                key: 'user_profile',
                titleKey: 'menu.user.profile',
                icon: 'Avatar',
                path: '/users/profile',
                roles: ['ADMIN', 'TEACHER']
            }
        ]
    },
    {
        key: 'event',
        titleKey: 'menu.event.root',
        icon: 'Trophy',
        path: '/events',
        roles: ['ADMIN', 'TEACHER'],
        children: [
            {
                key: 'event_list',
                titleKey: 'menu.event.list',
                icon: 'Calendar',
                path: '/events/list'
            },
            {
                key: 'event_signup',
                titleKey: 'menu.event.signup',
                icon: 'Checked',
                path: '/events/signups'
            },
            {
                key: 'event_result',
                titleKey: 'menu.event.result',
                icon: 'Medal',
                path: '/events/results'
            }
        ]
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
