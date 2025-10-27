import { createI18n } from 'vue-i18n'
import { messages, defaultLocale } from '@/locale'

const i18n = createI18n({
    locale: uni.getStorageSync('language') || defaultLocale,
    fallbackLocale: defaultLocale,
    messages,
    legacy: false, // 使用组合式API
    globalInjection: true, // 全局注入 $t 等方法
    missingWarn: false, // 关闭警告
    fallbackWarn: false
})

export const useI18n = () => {
    const setLocale = (locale) => {
        i18n.global.locale = locale
        uni.setStorageSync('language', locale)
    }

    const getLocale = () => i18n.global.locale

    return {
        i18n,
        setLocale,
        getLocale
    }
}

export default i18n
