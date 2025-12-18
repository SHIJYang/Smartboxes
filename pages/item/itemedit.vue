<template>
  <view class="page-container">
    <view class="bg-shape shape-1"></view>

    <view class="content-wrapper">
      <view class="header fade-in-down">
        <text class="title">{{ form.id ? '编辑物品' : '添加物品' }}</text>
        <text class="subtitle">记录每一件心爱之物</text>
      </view>

      <view class="form-card fade-in-up">
        <view class="input-group">
          <view class="label">物品名称</view>
          <view class="input-wrapper">
            <text class="icon">✨</text>
            <input class="inp" v-model="form.itemName" placeholder="例如：Switch 游戏机" placeholder-class="placeholder" />
          </view>
        </view>

        <view class="input-group">
          <view class="label">分类标签</view>
          <view class="input-wrapper">
            <text class="icon">🏷️</text>
            <input class="inp" v-model="form.itemTag" placeholder="例如：数码 / 衣物" placeholder-class="placeholder" />
          </view>
        </view>
        
        <view class="input-group">
          <view class="label">价值 (元)</view>
          <view class="input-wrapper">
            <text class="icon">💰</text>
            <input class="inp" type="digit" v-model.number="form.price" placeholder="0.00" placeholder-class="placeholder" />
          </view>
        </view>

        <view class="input-group">
          <view class="label">备注信息</view>
          <view class="textarea-wrapper">
            <textarea class="area" v-model="form.itemDesc" placeholder="写点什么..." placeholder-class="placeholder" auto-height />
          </view>
        </view>
      </view>

      <view class="action-area fade-in-up">
        <button class="btn-save" hover-class="btn-hover" @click="submit" :loading="submitting">保 存</button>
        <button v-if="form.id" class="btn-del" hover-class="btn-hover" @click="remove">删除物品</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { saveItem, getItemDetail, deleteItem } from '@/api/index';
import type { ItemDTO } from '@/common/types';

const form = ref<ItemDTO>({ id: 0, boxId: 0, itemName: '', price: 0, itemTag: '', itemDesc: '' });
const submitting = ref(false);

onLoad(async (opt: any) => {
  if (opt.boxId) form.value.boxId = parseInt(opt.boxId);
  if (opt.id) {
    uni.setNavigationBarTitle({ title: '编辑物品' });
    const res = await getItemDetail(parseInt(opt.id));
    if (res.code === 200) form.value = res.data;
  } else {
    uni.setNavigationBarTitle({ title: '添加物品' });
  }
});

const submit = async () => {
  if (!form.value.itemName) return uni.showToast({ title: '请输入名称', icon: 'none' });
  
  submitting.value = true;
  try {
    await saveItem(form.value);
    uni.showToast({ title: '保存成功', icon: 'success' });
    setTimeout(() => uni.navigateBack(), 800);
  } finally {
    submitting.value = false;
  }
};

const remove = async () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要移除这个物品吗？',
    confirmColor: '#ff6b81',
    success: async (res) => {
      if (res.confirm) {
        await deleteItem(form.value.id);
        uni.navigateBack();
      }
    }
  });
};
</script>

<style lang="scss" scoped>
/* 保持与 boxedit.vue 一致的样式变量 */
$primary-color: #4facfe;
$glass-bg: rgba(255, 255, 255, 0.85);

.page-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef2f3 100%);
  padding: 40rpx;
  position: relative;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
}
.shape-1 { width: 300rpx; height: 300rpx; background: rgba(161, 140, 209, 0.15); top: -50rpx; right: -50rpx; }

.content-wrapper { position: relative; z-index: 10; }

.header {
  margin-bottom: 50rpx;
  .title { font-size: 48rpx; font-weight: 800; color: #333; margin-bottom: 10rpx; display: block; }
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
  .label { font-size: 28rpx; color: #666; margin-bottom: 16rpx; font-weight: 500; }
  
  .input-wrapper {
    display: flex;
    align-items: center;
    border-bottom: 2rpx solid #eee;
    padding-bottom: 10rpx;
    transition: border-color 0.3s;
    
    .icon { font-size: 34rpx; margin-right: 20rpx; }
    .inp { flex: 1; font-size: 32rpx; color: #333; height: 60rpx; }
    
    &:focus-within { border-bottom-color: $primary-color; }
  }
  
  .textarea-wrapper {
    background: #f9f9f9;
    border-radius: 20rpx;
    padding: 20rpx;
    .area { width: 100%; min-height: 100rpx; font-size: 30rpx; color: #333; }
  }
}

.placeholder { color: #ccc; font-size: 28rpx; }

.action-area {
  margin-top: 60rpx;
  .btn-save {
    background: linear-gradient(90deg, #a18cd1, #fbc2eb); /* 使用稍微不同的柔和渐变 */
    color: #fff;
    border-radius: 50rpx;
    font-size: 32rpx;
    font-weight: bold;
    box-shadow: 0 10rpx 20rpx rgba(161, 140, 209, 0.3);
    margin-bottom: 30rpx;
    border: none;
    &::after { border: none; }
  }
  
  .btn-del {
    background: rgba(255,255,255,0.6);
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