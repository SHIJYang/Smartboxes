<template>
  <!-- 使用 uview-plus 全局组件做一个最小示例 -->
  <u-navbar title="模拟分类收纳" />
  <view class="app-shell">
    <view class="welcome">
      <text>欢迎使用 模拟分类收纳 管理系统</text>
    </view>
    <view class="actions">
      <u-button type="primary" @click="openDemo">打开示例</u-button>
    </view>
  </view>
</template>

<script>
import { useTheme } from "@/utils/theme";
import i18n from "@/utils/i18n";

export default {
  onLaunch() {
    const { getThemeConfig } = useTheme();
    // 初始化主题
    const theme = getThemeConfig();
    this.updateThemeVariables(theme);
  },
  methods: {
    updateThemeVariables(theme) {
      // 增加 document 存在性检查，避免在非 H5 环境报错
      if (typeof document === "undefined" || !theme) return;
      // 更新CSS变量
      Object.entries(theme).forEach(([key, value]) => {
        document.documentElement.style.setProperty(`--${key}-color`, value);
      });
    },
    openDemo() {
      // 示例交互，使用 uview-plus 组件触发
      uni.showToast({ title: "示例已打开", icon: "none" });
    },
  },
};
</script>

<style lang="scss">
@import "@/theme/variables.scss";

// 全局样式
.app-shell {
  padding: 16px;
  background-color: var(--background-color);
  color: var(--text-primary-color);
  min-height: 100vh;
}
.welcome {
  margin: 18px 0;
  font-size: 16px;
}
.actions {
  margin-top: 12px;
}
</style>
