<template>
  <div class="home-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="aside-container">
        <div class="logo">
          <h1>智能收纳盒系统</h1>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          @select="handleMenuSelect"
        >
          <el-sub-menu index="1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="1-1">
              <router-link to="/users">用户列表</router-link>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2">
            <template #title>
              <el-icon><Box /></el-icon>
              <span>盒子管理</span>
            </template>
            <el-menu-item index="2-1">
              <router-link to="/boxes">盒子列表</router-link>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="3">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="3-1">
              <router-link to="/items">商品列表</router-link>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="4">
            <template #title>
              <el-icon><CollectionTag /></el-icon>
              <span>情感标签管理</span>
            </template>
            <el-menu-item index="4-1">
              <router-link to="/emotion-tags">标签列表</router-link>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="5">
            <template #title>
              <el-icon><ChatDotRound /></el-icon>
              <span>AI聊天</span>
            </template>
            <el-menu-item index="5-1">
              <router-link to="/ai-chat">聊天界面</router-link>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="6">
            <template #title>
              <el-icon><Warning /></el-icon>
              <span>错误修复管理</span>
            </template>
            <el-menu-item index="6-1">
              <router-link to="/error-recovery">修复列表</router-link>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header-container">
          <div class="header-left">
            <el-button type="text" class="menu-toggle">
              <el-icon><Menu /></el-icon>
            </el-button>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="dropdown-trigger">
                <el-avatar size="small">
                  {{ currentUser ? currentUser.userName[0] : 'U' }}
                </el-avatar>
                <span class="user-name">{{ currentUser ? currentUser.userName : '未登录' }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主内容区 -->
        <el-main class="main-container">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import {
  User, Box, Goods, CollectionTag, ChatDotRound, Warning,
  Menu, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const currentUser = computed(() => userStore.currentUser)

// 获取当前激活的菜单
const activeMenu = computed(() => {
  const path = router.currentRoute.value.path
  if (path.includes('/users')) return '1-1'
  if (path.includes('/boxes')) return '2-1'
  if (path.includes('/items')) return '3-1'
  if (path.includes('/emotion-tags')) return '4-1'
  if (path.includes('/ai-chat')) return '5-1'
  if (path.includes('/error-recovery')) return '6-1'
  return '1-1'
})

// 处理菜单选择
const handleMenuSelect = (index) => {
  console.log('Selected menu:', index)
}

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.aside-container {
  background-color: #001529;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #1f2d3d;
}

.logo h1 {
  color: white;
  font-size: 18px;
  margin: 0;
}

.el-menu-vertical-demo {
  background-color: #001529;
  border-right: none;
  height: calc(100vh - 60px);
}

.el-menu-vertical-demo .el-menu-item, .el-menu-vertical-demo .el-sub-menu__title {
  color: rgba(255, 255, 255, 0.85);
}

.el-menu-vertical-demo .el-menu-item:hover, .el-menu-vertical-demo .el-sub-menu__title:hover {
  background-color: #1f2d3d;
}

.el-menu-vertical-demo .el-menu-item.is-active {
  background-color: #1890ff;
  color: white;
}

.header-container {
  background-color: white;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-left: 200px;
}

.header-left {
  display: flex;
  align-items: center;
}

.menu-toggle {
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-name {
  margin: 0 10px;
}

.main-container {
  margin-left: 200px;
  padding: 20px;
  background-color: #f5f7fa;
  height: calc(100vh - 60px);
  overflow-y: auto;
}
</style>