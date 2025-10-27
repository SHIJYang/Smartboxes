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
      // 更新CSS变量
      Object.entries(theme).forEach(([key, value]) => {
        document.documentElement.style.setProperty(`--${key}-color`, value);
      });
    },
  },
};
</script>

<style lang="scss">
@import "@/theme/variables.scss";

// 全局样式
page {
  background-color: var(--background-color);
  color: var(--text-primary-color);
}
</style>
