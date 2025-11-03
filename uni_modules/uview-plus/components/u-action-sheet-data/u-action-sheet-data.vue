<template>
	<view class="u-action-sheet-data">
		<view class="u-action-sheet-data__trigger">
			<slot name="trigger"></slot>
			<u-input
				v-if="!$slots['trigger']"
				:modelValue="current"
				disabled
				disabledColor="#ffffff"
				:placeholder="title"
				border="none"
			></u-input>
			<view @click="show = true"
				class="u-action-sheet-data__trigger__cover"></view>
		</view>
		<u-action-sheet
			:show="show"
			:actions="options"
			:title="title"
			safeAreaInsetBottom
			:description="description"
			@close="show = false"
			@select="select"
		>
		</u-action-sheet>
	</view>
</template>

<script>
export default {
    props: {
		modelValue: {
			type: [String, Number],
			default: ''
		},
		title: {
			type: String,
			default: ''
		},
		description: {
			type: String,
			default: ''
		},
		options: {
			type: Array,
			default: () => {
				return []
			}
		},
		valueKey: {
			type: String,
			default: 'value'
		},
		labelKey: {
			type: String,
			default: 'name'
		}
    },
    data() {
        return {
			show: false,
			current: '',
        }
    },
    created() {
		if (this.modelValue) {
			this.options.forEach((ele) => {
				if (ele[this.valueKey] == this.modelValue) {
					this.current = ele[this.labelKey]
				}
			})
		}
    },
    emits: ['update:modelValue'],
	watch: {
		modelValue() {
			this.options.forEach((ele) => {
				if (ele[this.valueKey] == this.modelValue) {
					this.current = ele[this.labelKey]
				}
			})
		}
	},
    methods: {
        hideKeyboard() {
            uni.hideKeyboard()
        },
        select(e) {
            this.$emit('update:modelValue', e[this.valueKey])
			this.current = e[this.labelKey]
        },
    }
}
</script>

<style lang="scss" scoped>
	.u-action-sheet-data {
		&__trigger {
			position: relative;
			&__cover {
				position: absolute;
				top: 0;
				left: 0;
				right: 0;
				bottom: 0;
			}
		}
	}
</style>