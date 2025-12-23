<template>
  <view class="pc-nav-wrapper">
    <view class="pc-nav-content">
      <view class="logo" @click="go('/pages/index/index')">
        <text class="emoji">✨</text>
        <text class="title">MagicBox</text>
      </view>

      <view class="menu">
        <view class="menu-item" :class="{active: current === 'index'}" @click="go('/pages/index/index')">大厅</view>
        <view class="menu-item" :class="{active: current === 'box'}" @click="go('/pages/box/boxlist')">盒子</view>
        <view class="menu-item" :class="{active: current === 'chat'}" @click="go('/pages/chat/chat')">助手</view>
        <view class="menu-item" :class="{active: current === 'user'}" @click="go('/pages/user/user')">我的</view>
      </view>

      <view class="actions">
        <view class="search-pill" @click="go('/pages/item/itemlist')">
          <text>🔍</text>
        </view>
        <view class="avatar-circle" @click="go('/pages/user/user')"></view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  current: { type: String, default: 'index' }
});

const go = (url) => {
  // 智能跳转：如果是 Tab 页面则 switchTab，否则 navigateTo
  uni.switchTab({
    url,
    fail: () => {
      uni.navigateTo({ url });
    }
  });
};
</script>

<style scoped lang="scss">
/* 核心修复逻辑：
  1. 默认状态下 (手机/平板竖屏) 强制隐藏
  2. 只有宽度大于 960px (标准PC/横屏平板) 时才显示
*/
.pc-nav-wrapper {
  display: none !important; /* 强制隐藏，防止被 Flex 布局撑开 */
  position: fixed;
  top: 0; 
  left: 0; 
  right: 0;
  height: 70px;
  background: rgba(255, 255, 255, 0.95); /* 略微增加不透明度 */
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  z-index: 999;

  /* PC 端断点判定 */
  @media screen and (min-width: 960px) {
    display: block !important;
  }
}

.pc-nav-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex; align-items: center; cursor: pointer;
  transition: opacity 0.2s;
  &:hover { opacity: 0.8; }
  
  .emoji { font-size: 28px; margin-right: 8px; }
  .title { font-size: 22px; font-weight: 800; color: #333; letter-spacing: -0.5px; }
}

.menu {
  display: flex; gap: 40px;
  .menu-item {
    font-size: 16px; font-weight: bold; color: #888; cursor: pointer; position: relative;
    transition: color 0.3s;
    
    &.active { color: #FF9A9E; }
    
    /* 激活状态的小圆点 */
    &.active::after {
      content: ''; position: absolute; bottom: -8px; left: 50%; transform: translateX(-50%);
      width: 6px; height: 6px; background: #FF9A9E; border-radius: 50%;
    }
    
    &:hover { color: #FF9A9E; }
  }
}

.actions {
  display: flex; align-items: center; gap: 20px;
  
  .search-pill {
    background: #f5f5f7; color: #666; padding: 8px 16px; border-radius: 20px; font-size: 14px;
    cursor: pointer; transition: all 0.2s;
    &:hover { background: #e0e0e0; }
  }
  
  .avatar-circle {
    width: 38px; height: 38px; 
    background: linear-gradient(135deg, #a18cd1, #fbc2eb); 
    border-radius: 50%; 
    cursor: pointer;
    box-shadow: 0 4px 10px rgba(161, 140, 209, 0.4);
    transition: transform 0.2s;
    &:hover { transform: scale(1.05); }
  }
}
</style>