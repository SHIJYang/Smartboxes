<template>
  <view class="page-container">
    <PCHeader current="box" />
    <view class="pc-placeholder"></view>

    <view class="bg-shape shape-1"></view>

    <view class="content-wrapper">
      <view class="header fade-in-down">
        <text class="title">{{ form.id ? '编辑盒子' : '新建盒子' }}</text>
        <text class="subtitle">{{ form.id ? '修改属性' : 'Create New Box' }}</text>
      </view>

      <view class="form-card fade-in-up">
        <view class="input-group">
          <view class="label">盒子名称</view>
          <view class="input-shell">
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
          <view class="label">唯一编码 (Code)</view>
          <view class="input-shell">
            <text class="icon">🔢</text>
            <input 
              class="inp" 
              v-model="form.boxCode" 
              placeholder="设备背面的 ID" 
              placeholder-class="placeholder"
            />
          </view>
        </view>
        
        <view class="input-group" v-if="form.id">
          <view class="label">状态模拟</view>
          <view class="switch-row">
             <text>设备在线</text>
             <switch 
               :checked="form.status === 1" 
               @change="e => form.status = e.detail.value ? 1 : 0" 
               color="#FF9A9E" 
               style="transform:scale(0.8)"
             />
          </view>
        </view>
      </view>

      <view class="action-area fade-in-up">
        <button class="btn-save" hover-class="btn-hover" @click="submit">
          {{ submitting ? '保存中...' : '保 存' }}
        </button>
        
        <button v-if="form.id" class="btn-del" hover-class="btn-hover" @click="remove">
          删除
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
import PCHeader from '@/components/PCHeader.vue';

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
    confirmColor: '#FF9A9E',
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
/* 统一暖色变量 */
$bg-color: #FFF9F0;
$primary-gradient: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);

.page-container {
  min-height: 100vh;
  background-color: $bg-color;
  padding: 40rpx;
  position: relative;
  overflow: hidden;
}

.pc-placeholder {
  display: none; height: 80px;
  @media screen and (min-width: 768px) { display: block; }
}

.bg-shape {
  position: absolute;
  width: 400rpx; height: 400rpx;
  background: radial-gradient(circle, rgba(251, 194, 235, 0.2) 0%, rgba(255,255,255,0) 70%);
  border-radius: 50%;
  top: -50rpx; right: -80rpx;
  z-index: 0; pointer-events: none;
}

.content-wrapper { position: relative; z-index: 10; }

.header {
  margin-bottom: 50rpx;
  .title { font-size: 56rpx; font-weight: 800; color: #333; display: block; margin-bottom: 10rpx; }
  .subtitle { font-size: 26rpx; color: #999; }
}

.form-card {
  background: #fff;
  border-radius: 40rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(161, 140, 209, 0.1);
}

.input-group {
  margin-bottom: 40rpx;
  &:last-child { margin-bottom: 0; }
  
  .label { font-size: 28rpx; color: #666; margin-bottom: 16rpx; font-weight: bold; padding-left: 10rpx;}
  
  /* 奶油风输入框：浅灰色胶囊背景 */
  .input-shell {
    background: #F8F8F8;
    border-radius: 24rpx;
    padding: 20rpx 30rpx;
    display: flex; align-items: center;
    border: 2rpx solid transparent;
    transition: all 0.3s;
    
    .icon { font-size: 36rpx; margin-right: 20rpx; }
    .inp { flex: 1; font-size: 30rpx; color: #333; height: 40rpx; }
    .placeholder { color: #ccc; }
    
    &:focus-within { background: #fff; border-color: #FF9A9E; box-shadow: 0 4rpx 10rpx rgba(255, 154, 158, 0.2); }
  }
  
  .switch-row {
    display: flex; justify-content: space-between; align-items: center;
    padding: 0 10rpx; font-size: 30rpx; font-weight: bold; color: #555;
  }
}

.action-area {
  margin-top: 60rpx;
  
  .btn-save {
    background: $primary-gradient;
    color: #fff;
    border-radius: 50rpx;
    font-size: 34rpx; font-weight: bold;
    box-shadow: 0 10rpx 20rpx rgba(255, 154, 158, 0.3);
    margin-bottom: 30rpx;
    border: none;
    height: 100rpx; line-height: 100rpx;
    &::after { border: none; }
  }
  
  .btn-del {
    background: #fff;
    color: #ff6b81;
    border: 2rpx solid #ffebee;
    border-radius: 50rpx;
    font-size: 30rpx; font-weight: bold;
    height: 90rpx; line-height: 90rpx;
    &::after { border: none; }
  }
  
  .btn-hover { transform: scale(0.98); opacity: 0.9; }
}

.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>