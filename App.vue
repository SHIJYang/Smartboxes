<template>
  <u-navbar title="模拟分类收纳" />
  <view class="app-shell">
    <view class="welcome" ref="welcomeRef">
      <text>欢迎使用 模拟分类收纳 管理系统</text>
    </view>
    <view class="actions">
      <u-button type="primary" @click="openDemo">打开示例</u-button>
      <u-button type="default" @click="toggleTheme">切换主题</u-button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useTheme } from "@/utils/theme";
import gsap from "gsap";

const welcomeRef = ref(null);
const { currentTheme, setTheme, getThemeConfig } = useTheme();

const updateThemeVariables = (theme) => {
  if (typeof document === "undefined" || !theme) return;
  Object.entries(theme).forEach(([key, value]) => {
    document.documentElement.style.setProperty(`--${key}-color`, value);
  });
};

onMounted(() => {
  updateThemeVariables(getThemeConfig());
  if (welcomeRef.value) {
    gsap.fromTo(
      welcomeRef.value,
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 1 }
    );
  }
});

watch(currentTheme, () => {
  updateThemeVariables(getThemeConfig());
});

const openDemo = () => {
  uni.showToast({ title: "示例已打开", icon: "none" });
};

const toggleTheme = () => {
  const next =
    currentTheme.value === "default"
      ? "dark"
      : currentTheme.value === "dark"
      ? "elegant"
      : "default";
  setTheme(next);
  uni.showToast({ title: `已切换主题：${next}`, icon: "none" });
};
</script>

<style lang="scss">
@import "uview-plus/libs/css/common.scss";
@import "@/theme/variables.scss";
@import "uview-plus/index.scss";

// 全局样式
.app-shell {
  padding: 16px;
  background-color: var(--background-color);
  color: var(--text-primary-color);
  min-height: 100vh;
}
.welcome {
  margin: 18px 0;
  font-size: 18px;
  text-align: center;
}
.actions {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
