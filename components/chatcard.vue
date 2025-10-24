<!-- components/语音输入按钮.vue -->
<template>
  <view class="voice-btn-wrapper">
    <u-button
      :type="isRecording ? 'error' : 'primary'"
      shape="circle"
      size="large"
      :disabled="disabled"
      @touchstart="startRecord"
      @touchend="stopRecord"
      @mouseleave="stopRecord"
      class="voice-btn"
    >
      <u-icon
        :name="isRecording ? 'stop-circle' : 'mic'"
        size="48"
        :color="isRecording ? '#fff' : '#fff'"
      />
    </u-button>
    <text v-if="isRecording" class="recording-text">松开结束</text>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['start', 'stop'])

const isRecording = ref(false)

const startRecord = () => {
  if (props.disabled) return
  isRecording.value = true
  emit('start')
}

const stopRecord = () => {
  if (!isRecording.value) return
  isRecording.value = false
  emit('stop')
}
</script>

<style scoped>
.voice-btn-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.voice-btn {
  width: 120rpx;
  height: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.recording-text {
  font-size: 24rpx;
  color: #fa3534;
  margin-top: 12rpx;
}
</style>