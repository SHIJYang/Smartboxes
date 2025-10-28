import {
	defineConfig
} from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import {
	resolve
} from 'path'
// 引入vue插件（uni内部依赖依赖，但vue，但需要显式声明配置）
import vue from '@vitejs/plugin-vue'

export default defineConfig({
	plugins: [

		uni({
			vueOptions: {
				template: {
					compilerOptions: {
						// 关键配置：声明所有 up- 开头的标签为自定义元素
						isCustomElement: (tag) => tag.startsWith('up-')
					}
				}
			}
		}) // uni插件放在后面
	],
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
				additionalData: `@import "uview-plus/libs/css/common.scss";`
			}
		}
	}
})