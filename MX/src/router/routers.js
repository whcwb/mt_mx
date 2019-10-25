import Main from '@/view/main'

/**
 * iview-admin中meta除了原生参数外可配置的参数:
 * meta: {
 *  hideInMenu: (false) 设为true后在左侧菜单不会显示该页面选项
 *  notCache: (false) 设为true后页面不会缓存
 *  access: (null) 可访问该页面的权限数组，当前路由设置的权限会影响子路由
 *  icon: (-) 该页面在左侧菜单、面包屑和标签导航处显示的图标，如果是自定义图标，需要在图标名称前加下划线'_'
 * }
 */

export default [
  {
    path: '/login',
    name: 'login',
    meta: {
      title: 'Login - 登录',
      hideInMenu: true
    },
    component: () => import('@/view/login/login.vue')
  },
  {
    path: '/',
    name: 'home',
    redirect: '/home',
    component: Main,
    meta: {
      hideInMenu: true,
      notCache: true
    },
    children: [
      // {
      //   path: '/home',
      //   name: 'home',
      //   meta: {
      //     hideInMenu: true,
      //     title: '工作台',
      //     notCache: true
      //   },
      //   component: () => import('@/view/single-page/home')
      // }
      {
        path: '/home',
        name: 'home',
        meta: {
          hideInMenu: true,
          title: '练车费维护',
          notCache: true
        },
        component: () => import('@/view/lcsf/lcdjConfig')
      },
    ]
  },
  {
    path: '/system',
    name: 'system',
    meta: {
      icon: 'logo-buffer',
      title: '系统设置'
    },
    component: Main,
    children: [
      {
        path: 'system-user',
        name: 'system-user',
        meta: {
          icon: 'md-trending-up',
          title: '用户管理'
        },
        component: () => import('@/view/system/system-user')
      },
      {
        path: 'system-role',
        name: 'system-role',
        meta: {
          icon: '_bear',
          title: '角色管理'
        },
        component: () => import('@/view/system/system-root')
      },
      {
        path: 'system-framework',
        name: 'system-framework',
        meta: {
          icon: '_bear',
          title: '机构管理'
        },
        component: () => import('@/view/system/system-framework')
      },
      {
        path: 'system-function',
        name: 'system-function',
        meta: {
          icon: '_bear',
          title: '功能管理'
        },
        component: () => import('@/view/system/system-function')
      },
      {
        path: 'system_Jurisdiction',
        name: 'system_Jurisdiction',
        meta: {
          icon: '_bear',
          title: '功能管理'
        },
        component: () => import('@/view/system/system-Jurisdiction')
      },
      {
        path: 'system-daily',
        name: 'system-daily',
        meta: {
          icon: '_bear',
          title: '日志管理'
        },
        component: () => import('@/view/system/system-daily')
      },
      {
        path: 'dictionaries',
        name: 'dictionaries',
        meta: {
          icon: '_bear',
          title: '字典管理'
        },
        component: () => import('@/view/system/dictionaries')
      },
      {
        path: 'system-ShortMessage',
        name: 'system-ShortMessage',
        meta: {
          icon: '_bear',
          title: '短信管理'
        },
        component: () => import('@/view/system/system-ShortMessage')
      }
    ]
  },
  {
    path: '/lcsf',
    name: 'lcsf',
    meta: {
      icon: 'logo-buffer',
      title: '练车管理'
    },
    component: Main,
    children: [
      {
        path: 'clwh',
        name: 'clwh',
        meta: {
          icon: '_bear',
          title: '考试车维护'
        },
        component: () => import('@/view/lcsf/clgl')
      },
      {
        path: 'lcjl',
        name: 'lcjl',
        meta: {
          icon: '_bear',
          title: '模训记录表'
        },
        component: () => import('@/view/lcsf/lcjl')
      },
      {
        path: 'rebate',
        name: 'rebate',
        meta: {
          icon: '_bear',
          title: '返点确认'
        },
        component: () => import('@/view/charge/rebate')
      },
      {
        path: 'lcsf-statistics',
        name: 'lcsf-statistics',
        meta: {
          icon: '_bear',
          title: '模训统计'
        },
        component: () => import('@/view/lcsf/statistics')
      },
      {
        path: 'lcdjConfig',
        name: 'lcdjConfig',
        meta: {
          icon: '_bear',
          title: '练车费维护'
        },
        component: () => import('@/view/lcsf/lcdjConfig')
      },
      {
        path: 'keerlianche',
        name: 'keerlianche',
        meta: {
          icon: '_bear',
          title: '科目二模训'
        },
        component: () => import('@/view/lcsf/keerlianche')
      },
      {
        path: 'kesanlianche',
        name: 'kesanlianche',
        meta: {
          icon: '_bear',
          title: '科目三模训'
        },
        component: () => import('@/view/lcsf/kesanlianche')
      },
      {
        path: 'aqygl',
        name: 'aqygl',
        meta: {
          icon: '_bear',
          title: '安全员管理'
        },
        component: () => import('@/view/lcsf/aqygl')
      },
      {
        path: 'aqytj',
        name: 'aqytj',
        meta: {
          icon: '_bear',
          title: '安全员统计'
        },
        component: () => import('@/view/lcsf/aqytj')
      },
      {
        path: 'jlytj',
        name: 'jlytj',
        meta: {
          icon: '_bear',
          title: '教练员统计'
        },
        component: () => import('@/view/lcsf/jlytj')
      },
      {
        path: 'jxtj',
        name: 'jxtj',
        meta: {
          icon: '_bear',
          title: '驾校统计'
        },
        component: () => import('@/view/lcsf/jxtj')
      },
      {
        path: 'kmeqd',
        name: 'kmeqd',
        meta: {
          icon: 'ios-create-outline',
          title: '科目二签到'
        },
        component: () => import('@/view/lcsf/kmeqd')
      },
      {
        path: 'kmsqd',
        name: 'kmsqd',
        meta: {
          icon: 'ios-create-outline',
          title: '安全员签到'
        },
        component: () => import('@/view/lcsf/kmsqd')
      },
      {
        path: 'kmetj',
        name: 'kmetj',
        meta: {
          icon: 'ios-create-outline',
          title: '科目二统计'
        },
        component: () => import('@/view/lcsf/kmetj')
      },
      {
        path: 'kmstj',
        name: 'kmstj',
        meta: {
          icon: 'ios-create-outline',
          title: '科目三统计'
        },
        component: () => import('@/view/lcsf/kmstj')
      }
    ]
  },
  // {
  //   path: '',
  //   name: 'doc',
  //   meta: {
  //     title: '文档',
  //     href: 'https://lison16.github.io/iview-admin-doc/#/',
  //     icon: 'ios-book'
  //   }
  // },
  // {
  //   path: '/join',
  //   name: 'join',
  //   component: Main,
  //   children: [
  //     {
  //       path: 'join_page',
  //       name: 'join_page',
  //       meta: {
  //         icon: '_qq',
  //         title: 'QQ群'
  //       },
  //       component: () => import('@/view/join-page.vue')
  //     }
  //   ]
  // },
  // {
  //   path: '/components',
  //   name: 'components',
  //   meta: {
  //     icon: 'logo-buffer',
  //     title: '组件'
  //   },
  //   component: Main,
  //   children: [
  //     {
  //       path: 'count_to_page',
  //       name: 'count_to_page',
  //       meta: {
  //         icon: 'md-trending-up',
  //         title: '数字渐变'
  //       },
  //       component: () => import('@/view/components/count-to/count-to.vue')
  //     },
  //     {
  //       path: 'tables_page',
  //       name: 'tables_page',
  //       meta: {
  //         icon: 'md-grid',
  //         title: '多功能表格'
  //       },
  //       component: () => import('@/view/components/tables/tables.vue')
  //     },
  //     {
  //       path: 'split_pane_page',
  //       name: 'split_pane_page',
  //       meta: {
  //         icon: 'md-pause',
  //         title: '分割窗口'
  //       },
  //       component: () => import('@/view/components/split-pane/split-pane.vue')
  //     },
  //     {
  //       path: 'markdown_page',
  //       name: 'markdown_page',
  //       meta: {
  //         icon: 'logo-markdown',
  //         title: 'Markdown编辑器'
  //       },
  //       component: () => import('@/view/components/markdown/markdown.vue')
  //     },
  //     {
  //       path: 'editor_page',
  //       name: 'editor_page',
  //       meta: {
  //         icon: 'ios-create',
  //         title: '富文本编辑器'
  //       },
  //       component: () => import('@/view/components/editor/editor.vue')
  //     },
  //     {
  //       path: 'icons_page',
  //       name: 'icons_page',
  //       meta: {
  //         icon: '_bear',
  //         title: '自定义图标'
  //       },
  //       component: () => import('@/view/components/icons/icons.vue')
  //     }
  //   ]
  // },
  // {
  //   path: '/update',
  //   name: 'update',
  //   meta: {
  //     icon: 'md-cloud-upload',
  //     title: '数据上传'
  //   },
  //   component: Main,
  //   children: [
  //     {
  //       path: 'update_table_page',
  //       name: 'update_table_page',
  //       meta: {
  //         icon: 'ios-document',
  //         title: '上传Csv'
  //       },
  //       component: () => import('@/view/update/update-table.vue')
  //     },
  //     {
  //       path: 'update_paste_page',
  //       name: 'update_paste_page',
  //       meta: {
  //         icon: 'md-clipboard',
  //         title: '粘贴表格数据'
  //       },
  //       component: () => import('@/view/update/update-paste.vue')
  //     }
  //   ]
  // },
  // {
  //   path: '/directive',
  //   name: 'directive',
  //   meta: {
  //     hide: true
  //   },
  //   component: Main,
  //   children: [
  //     {
  //       path: 'directive_page',
  //       name: 'directive_page',
  //       meta: {
  //         icon: 'ios-navigate',
  //         title: '指令'
  //       },
  //       component: () => import('@/view/directive/directive.vue')
  //     }
  //   ]
  // },
  // {
  //   path: '/multilevel',
  //   name: 'multilevel',
  //   meta: {
  //     icon: 'md-menu',
  //     title: '多级菜单'
  //   },
  //   component: Main,
  //   children: [
  //     {
  //       path: 'level_2_1',
  //       name: 'level_2_1',
  //       meta: {
  //         icon: 'arrow-graph-up-right',
  //         title: '二级-1'
  //       },
  //       component: () => import('@/view/multilevel/level-2-1.vue')
  //     },
  //     {
  //       path: 'level_2_2',
  //       name: 'level_2_2',
  //       meta: {
  //         access: ['super_admin'],
  //         icon: 'arrow-graph-up-right',
  //         showAlways: true,
  //         title: '二级-2'
  //       },
  //       component: parentView,
  //       children: [
  //         {
  //           path: 'level_2_2_1',
  //           name: 'level_2_2_1',
  //           meta: {
  //             icon: 'arrow-graph-up-right',
  //             title: '三级'
  //           },
  //           component: () => import('@/view/multilevel/level-2-2/level-3-1.vue')
  //         }
  //       ]
  //     },
  //     {
  //       path: 'level_2_3',
  //       name: 'level_2_3',
  //       meta: {
  //         icon: 'arrow-graph-up-right',
  //         title: '二级-3'
  //       },
  //       component: () => import('@/view/multilevel/level-2-3.vue')
  //     },
  //   ]
  // },
  {
    path: '/401',
    name: 'error_401',
    meta: {
      hideInMenu: true
    },
    component: () => import('@/view/error-page/401.vue')
  },
  {
    path: '/500',
    name: 'error_500',
    meta: {
      hideInMenu: true
    },
    component: () => import('@/view/error-page/500.vue')
  },
  {
    path: '*',
    name: 'error_404',
    meta: {
      hideInMenu: true
    },
    component: () => import('@/view/error-page/404.vue')
  }
]
