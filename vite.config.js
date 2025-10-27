import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import { resolve } from 'path'

export default defineConfig({
    plugins: [uni()],
    resolve: {
        alias: {
            '@': resolve(__dirname, './src'),
            'uview-plus': resolve(__dirname, './node_modules/uview-plus'),
            'gsap': resolve(__dirname, './node_modules/gsap')
        }
    },
    define: {
        __VUE_I18N_FULL_INSTALL__: true,
        __VUE_I18N_LEGACY_API__: false,
        __INTLIFY_PROD_DEVTOOLS__: false,
        'process.env': {}
    },
    css: {
        preprocessorOptions: {
            scss: {
                additionalData: `@import "uview-plus/libs/css/variable.scss";`
            }
        }
    }
})