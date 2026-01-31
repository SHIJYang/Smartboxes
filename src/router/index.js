import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import UserList from '../views/user/UserList.vue'
import BoxList from '../views/box/BoxList.vue'
import ItemList from '../views/item/ItemList.vue'
import EmotionList from '../views/emotion/EmotionList.vue'
import AiChat from '../views/chat/AiChat.vue'
import ErrorRecovery from '../views/error/ErrorRecovery.vue'
import ResourceList from '../views/resource/ResourceList.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    children: [
      {
        path: 'users',
        name: 'UserList',
        component: UserList
      },
      {
        path: 'boxes',
        name: 'BoxList',
        component: BoxList
      },
      {
        path: 'items',
        name: 'ItemList',
        component: ItemList
      },
      {
        path: 'emotions',
        name: 'EmotionList',
        component: EmotionList
      },
      {
        path: 'chat',
        name: 'AiChat',
        component: AiChat
      },
      {
        path: 'error-recovery',
        name: 'ErrorRecovery',
        component: ErrorRecovery
      },
      {
        path: 'resources',
        name: 'ResourceList',
        component: ResourceList
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router