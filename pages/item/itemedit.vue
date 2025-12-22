<template>
  <view class="page-container">
    <PCHeader current="item" />
    <view class="pc-placeholder"></view>

    <view class="content-wrapper">
      <view class="header fade-in-down">
        <text class="title">{{ form.id ? '编辑物品' : '新物品入库' }}</text>
        <text class="subtitle">{{ form.id ? '修改物品信息' : '记录每一件心爱之物' }}</text>
      </view>

      <view class="form-card fade-in-up">
        <view class="input-group">
          <view class="label">物品名称</view>
          <view class="input-shell">
            <text class="icon">✨</text>
            <input 
              class="inp" 
              v-model="form.itemName" 
              placeholder="例如：Switch 游戏机" 
              placeholder-class="placeholder" 
            />
          </view>
        </view>

        <view class="input-group">
          <view class="label">放入哪个盒子 (ID)</view>
          <view class="input-shell">
            <text class="icon">📦</text>
            <input 
              class="inp" 
              type="number"
              v-model.number="form.boxId" 
              placeholder="输入盒子 ID (如: 1)" 
              placeholder-class="placeholder" 
            />
          </view>
        </view>

        <view class="input-group">
          <view class="label">分类标签</view>
          <view class="input-shell">
            <text class="icon">🏷️</text>
            <input 
              class="inp" 
              v-model="form.itemTag" 
              placeholder="例如：数码 / 衣物" 
              placeholder-class="placeholder" 
            />
          </view>
        </view>
        
        <view class="input-group">
          <view class="label">价值 (元)</view>
          <view class="input-shell">
            <text class="icon">💰</text>
            <input 
              class="inp" 
              type="digit" 
              v-model.number="form.price" 
              placeholder="0.00" 
              placeholder-class="placeholder" 
            />
          </view>
        </view>

        <view class="input-group">
          <view class="label">备注信息</view>
          <view class="textarea-shell">
            <textarea 
              class="area" 
              v-model="form.itemDesc" 
              placeholder="写点什么..." 
              placeholder-class="placeholder" 
              auto-height 
            />
          </view>
        </view>
      </view>

      <view class="action-area fade-in-up">
        <button class="btn-save" hover-class="btn-hover" @click="submit" :loading="submitting">
          {{ submitting ? '保存中...' : '保 存' }}
        </button>
        
        <button v-if="form.id" class="btn-del" hover-class="btn-hover" @click="remove">
          删除物品
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { saveItem, getItemDetail, deleteItem } from '@/api/index';
import type { ItemDTO } from '@/common/types';
import PCHeader from '@/components/PCHeader.vue';

// 默认数据
const form = ref<ItemDTO>({ 
  id: 0, 
  boxId: 1, 
  itemName: '', 
  price: undefined, 
  itemTag: '', 
  itemDesc: '' 
});
const submitting = ref(false);

onLoad(async (opt: any) => {
  // 如果 URL 带了 boxId，说明是从盒子详情页点进来的，自动填好
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
  if (!form.value.itemName) return uni.showToast({ title: '名字是必填的哦', icon: 'none' });
  if (!form.value.boxId) return uni.showToast({ title: '请指定一个盒子', icon: 'none' });
  
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
    title: '删除确认',
    content: '确定要丢弃这个物品记录吗？',
    confirmColor: '#FF9A9E',
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
/* 暖色主题 */
$bg-color: #FFF9F0;
$primary-gradient: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);

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

.content-wrapper { position: relative; z-index: 10; }

.header {
  margin-bottom: 50rpx;
  .title { font-size: 56rpx; font-weight: 800; color: #333; margin-bottom: 10rpx; display: block; }
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
  
  .label { font-size: 28rpx; color: #666; margin-bottom: 16rpx; font-weight: bold; padding-left: 10rpx; }
  
  /* 统一输入框样式 */
  .input-shell, .textarea-shell {
    background: #F8F8F8;
    border-radius: 24rpx;
    padding: 20rpx 30rpx;
    display: flex; align-items: center;
    border: 2rpx solid transparent;
    transition: all 0.3s;
    
    &:focus-within { background: #fff; border-color: #a18cd1; box-shadow: 0 4rpx 10rpx rgba(161, 140, 209, 0.2); }
  }
  
  .icon { font-size: 36rpx; margin-right: 20rpx; }
  .inp { flex: 1; font-size: 30rpx; color: #333; height: 40rpx; }
  .area { width: 100%; min-height: 100rpx; font-size: 30rpx; color: #333; }
  .placeholder { color: #ccc; }
}

.action-area {
  margin-top: 60rpx;
  .btn-save {
    background: $primary-gradient;
    color: #fff;
    border-radius: 50rpx;
    font-size: 34rpx; font-weight: bold;
    box-shadow: 0 10rpx 20rpx rgba(161, 140, 209, 0.3);
    margin-bottom: 30rpx;
    border: none;
    height: 100rpx; line-height: 100rpx;
    &::after { border: none; }
  }
  
  .btn-del {
    background: #fff; color: #ff6b81;
    border: 2rpx solid #ffebee; border-radius: 50rpx;
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