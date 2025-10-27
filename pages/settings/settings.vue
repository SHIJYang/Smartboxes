<template>
  <view class="settings-container" :class="{ 'platform-container': true }">
    <u-navbar title="设置" />

    <!-- 基本设置 -->
    <u-cell-group title="基本设置">
      <u-cell
        title="消息通知"
        @click="toggleNotification"
        :value="settings.notification ? '开启' : '关闭'"
      />
      <u-cell
        title="语音识别"
        @click="toggleVoice"
        :value="settings.voice ? '开启' : '关闭'"
      />
    </u-cell-group>

    <!-- 收纳设置 -->
    <u-cell-group title="收纳设置" margin-top="20">
      <u-cell
        title="默认收纳盒"
        :value="settings.defaultBox"
        @click="selectDefaultBox"
      />
      <u-cell
        title="自动分类"
        :value="settings.autoSort ? '开启' : '关闭'"
        @click="toggleAutoSort"
      />
    </u-cell-group>

    <!-- 账号安全 -->
    <u-cell-group title="账号安全" margin-top="20">
      <u-cell title="修改密码" @click="changePassword" />
      <u-cell title="隐私设置" @click="privacySettings" />
    </u-cell-group>

    <!-- 其他选项 -->
    <u-cell-group title="其他" margin-top="20">
      <u-cell title="清除缓存" :value="cacheSize" @click="clearCache" />
      <u-cell title="关于我们" @click="about" />
    </u-cell-group>

    <!-- 退出登录 -->
    <view class="logout-btn">
      <u-button type="error" @click="logout">退出登录</u-button>
    </view>

    <!-- 平台特定设置 -->
    <!-- #ifdef APP-PLUS -->
    <u-cell-group title="应用设置">
      <u-cell
        title="后台运行"
        @click="toggleBackground"
        :value="settings.background ? '开启' : '关闭'"
      />
      <u-cell title="位置权限" @click="checkPermission" />
    </u-cell-group>
    <!-- #endif -->

    <!-- #ifdef MP-WEIXIN -->
    <u-cell-group title="小程序设置">
      <button class="auth-btn" open-type="openSetting">
        <u-cell title="授权管理" />
      </button>
      <button class="auth-btn" open-type="contact">
        <u-cell title="联系客服" />
      </button>
    </u-cell-group>
    <!-- #endif -->

    <!-- 主题设置 -->
    <u-cell-group title="主题设置" margin-top="20">
      <u-cell
        title="主题选择"
        :value="themes[currentTheme].name"
        @click="showThemeSelect = true"
      />
    </u-cell-group>

    <!-- 主题选择弹窗 -->
    <u-popup v-model="showThemeSelect" mode="bottom">
      <view class="theme-select">
        <view
          v-for="(theme, name) in themes"
          :key="name"
          class="theme-item"
          :class="{ active: currentTheme === name }"
          @click="changeTheme(name)"
        >
          <text>{{ theme.name }}</text>
          <view
            class="theme-preview"
            :style="{ backgroundColor: theme.primary }"
          />
        </view>
      </view>
    </u-popup>

    <!-- 语言设置 -->
    <u-cell-group title="语言设置" margin-top="20">
      <u-cell
        :title="$t('settings.language')"
        :value="languages[currentLocale]"
        @click="showLanguageSelect = true"
      />
    </u-cell-group>

    <!-- 语言选择弹窗 -->
    <u-popup v-model="showLanguageSelect" mode="bottom">
      <view class="language-select">
        <view
          v-for="(name, code) in languages"
          :key="code"
          class="language-item"
          :class="{ active: currentLocale === code }"
          @click="changeLanguage(code)"
        >
          <text>{{ name }}</text>
          <u-icon v-if="currentLocale === code" name="checkmark" />
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import { ref } from "vue";
import { useTheme } from "@/utils/theme";
import { themes } from "@/theme/config";
import { useI18n } from "@/utils/i18n";

const settings = ref({
  notification: true,
  voice: true,
  defaultBox: "默认收纳盒",
  autoSort: true,
  background: true,
});

const cacheSize = ref("2.5MB");

const toggleNotification = () => {
  settings.value.notification = !settings.value.notification;
};

const toggleVoice = () => {
  settings.value.voice = !settings.value.voice;
};

const toggleAutoSort = () => {
  settings.value.autoSort = !settings.value.autoSort;
};

const selectDefaultBox = () => {
  // TODO: 选择默认收纳盒
};

const changePassword = () => {
  uni.navigateTo({ url: "/pages/password/change" });
};

const privacySettings = () => {
  uni.navigateTo({ url: "/pages/privacy/settings" });
};

const clearCache = () => {
  uni.showModal({
    title: "提示",
    content: "确定要清除缓存吗？",
    success: (res) => {
      if (res.confirm) {
        // TODO: 清除缓存
        uni.showToast({ title: "清除成功" });
        cacheSize.value = "0KB";
      }
    },
  });
};

const about = () => {
  uni.navigateTo({ url: "/pages/about/index" });
};

const logout = () => {
  uni.showModal({
    title: "提示",
    content: "确定要退出登录吗？",
    success: (res) => {
      if (res.confirm) {
        // TODO: 退出登录
        uni.reLaunch({ url: "/pages/login/login" });
      }
    },
  });
};

const { currentTheme, setTheme } = useTheme();
const showThemeSelect = ref(false);

const changeTheme = (themeName) => {
  setTheme(themeName);
  showThemeSelect.value = false;
};

const { getLocale, setLocale } = useI18n();
const currentLocale = ref(getLocale());
const showLanguageSelect = ref(false);

const languages = {
  "zh-CN": "简体中文",
  "en-US": "English",
  "ja-JP": "日本語",
};

const changeLanguage = (locale) => {
  setLocale(locale);
  currentLocale.value = locale;
  showLanguageSelect.value = false;
};

// APP专属方法
const checkPermission = async () => {
  if (!platform.isApp) return;
  const status = await uni.getSetting();
  // TODO: 检查权限
};
</script>

<style scoped>
.settings-container {
  min-height: 100vh;
  background: #f5f5f5;
}
.logout-btn {
  margin: 40rpx 30rpx;
}
</style>

<style lang="scss">
@import "@/theme/variables.scss";

.theme-select {
  padding: 30rpx;
}

.theme-item {
  @include themed-component;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 12rpx;

  &.active {
    border: 2rpx solid var(--primary-color);
  }
}

.theme-preview {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
}

.language-select {
  padding: 30rpx;
}

.language-item {
  @include themed-component;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 12rpx;

  &.active {
    color: var(--primary-color);
  }
}
</style>
