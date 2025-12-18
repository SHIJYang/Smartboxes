<template>
  <view class="page-container">
    <view class="bg-shape shape-1"></view>
    <view class="bg-shape shape-2"></view>

    <view class="content-wrapper">
      <view class="header fade-in-down">
        <text class="title">{{ form.id ? '编辑盒子' : '新建盒子' }}</text>
        <text class="subtitle">{{ form.id ? '修改盒子信息与状态' : '添加一个新的收纳容器' }}</text>
      </view>

      <view class="form-card fade-in-up">
        <view class="input-group">
          <view class="label">盒子名称</view>
          <view class="input-wrapper">
            <text class="icon">📦</text>
            <input 
              class="inp" 
              v-model="form.boxName" 
              placeholder="例如：客厅杂物箱" 
              placeholder-class="placeholder"
            />
          </view>
        </view>

        <view class="input-group">
          <view class="label">设备编号 (Code)</view>
          <view class="input-wrapper">
            <text class="icon">🔢</text>
            <input 
              class="inp" 
              v-model="form.boxCode" 
              placeholder="请输入设备唯一编码" 
              placeholder-class="placeholder"
            />
          </view>
        </view>
        
        <view class="input-group" v-if="form.id">
          <view class="label">状态模拟</view>
          <view class="switch-wrapper">
             <text>是否在线</text>
             <switch :checked="form.status === 1" @change="e => form.status = e.detail.value ? 1 : 0" color="#4facfe" style="transform:scale(0.8)"/>
          </view>
        </view>
      </view>

      <view class="action-area fade-in-up">
        <button class="btn-save" hover-class="btn-hover" @click="submit">
          {{ submitting ? '保存中...' : '保 存' }}
        </button>
        
        <button v-if="form.id" class="btn-del" hover-class="btn-hover" @click="remove">
          删除此盒子
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getBoxDetail, saveBox, deleteBox } from '@/api/index';
import type { BoxDTO } from '@/common/types';

const form = ref<BoxDTO>({ id: 0, boxName: '', boxCode: '', userId: 1001, status: 0, boxType: 1 });
const submitting = ref(false);

onLoad(async (opt: any) => {
  if (opt.id) {
    uni.setNavigationBarTitle({ title: '编辑盒子' });
    const res = await getBoxDetail(parseInt(opt.id));
    if (res.code === 200) form.value = res.data;
  } else {
    uni.setNavigationBarTitle({ title: '新建盒子' });
  }
});

const submit = async () => {
  if (!form.value.boxName || !form.value.boxCode) return uni.showToast({ title: '请填写完整', icon: 'none' });
  
  submitting.value = true;
  try {
    await saveBox(form.value);
    uni.showToast({ title: '保存成功', icon: 'success' });
    setTimeout(() => uni.navigateBack(), 800);
  } finally {
    submitting.value = false;
  }
};

const remove = async () => {
  uni.showModal({
    title: '确认删除',
    content: '删除后无法恢复，确定吗？',
    confirmColor: '#ff6b81',
    success: async (res) => {
      if (res.confirm) {
        await deleteBox(form.value.id);
        uni.navigateBack();
      }
    }
  });
};
</script>

<style lang="scss" scoped>
/* 引入公共变量 (建议放在 uni.scss 中，这里直接写) */
$primary-color: #4facfe;
$glass-bg: rgba(255, 255, 255, 0.85);

.page-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef2f3 100%);
  position: relative;
  padding: 40rpx;
  overflow: hidden;
}

/* 背景装饰 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
}
.shape-1 { width: 300rpx; height: 300rpx; background: rgba(79, 172, 254, 0.15); top: -60rpx; left: -60rpx; }
.shape-2 { width: 200rpx; height: 200rpx; background: rgba(255, 154, 158, 0.15); bottom: 100rpx; right: -50rpx; }

.content-wrapper { position: relative; z-index: 10; }

.header {
  margin-bottom: 50rpx;
  .title { font-size: 48rpx; font-weight: 800; color: #333; display: block; margin-bottom: 10rpx; }
  .subtitle { font-size: 26rpx; color: #999; }
}

.form-card {
  background: $glass-bg;
  backdrop-filter: blur(20px);
  border-radius: 40rpx;
  padding: 40rpx;
  box-shadow: 0 20rpx 40rpx rgba(0,0,0,0.05);
  border: 1px solid rgba(255,255,255,0.6);
}

.input-group {
  margin-bottom: 40rpx;
  &:last-child { margin-bottom: 0; }
  
  .label { font-size: 28rpx; color: #666; margin-bottom: 16rpx; font-weight: 500; }
  
  .input-wrapper {
    display: flex;
    align-items: center;
    border-bottom: 2rpx solid #eee;
    padding-bottom: 10rpx;
    transition: border-color 0.3s;
    
    .icon { font-size: 36rpx; margin-right: 20rpx; }
    .inp { flex: 1; font-size: 32rpx; color: #333; height: 60rpx; }
    .placeholder { color: #ccc; font-size: 28rpx; }
    
    &:focus-within { border-bottom-color: $primary-color; }
  }
  
  .switch-wrapper {
    display: flex; justify-content: space-between; align-items: center;
    font-size: 30rpx; color: #333;
  }
}

.action-area {
  margin-top: 60rpx;
  
  .btn-save {
    background: linear-gradient(90deg, #4facfe, #00f2fe);
    color: #fff;
    border-radius: 50rpx;
    font-size: 32rpx;
    font-weight: bold;
    box-shadow: 0 10rpx 20rpx rgba(79, 172, 254, 0.3);
    margin-bottom: 30rpx;
    border: none;
    
    &::after { border: none; }
  }
  
  .btn-del {
    background: rgba(255, 255, 255, 0.6);
    color: #ff6b81;
    border: 2rpx solid rgba(255, 107, 129, 0.3);
    border-radius: 50rpx;
    font-size: 30rpx;
    
    &::after { border: none; }
  }
  
  .btn-hover { transform: scale(0.98); opacity: 0.9; }
}

.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>