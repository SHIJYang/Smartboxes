<template>
  <view class="container">
    <u-navbar :title="spaceName" title-bold title-color="#333" />

    <view class="content">
      <u-empty
        v-if="itemList.length === 0 && !loading"
        text="暂无物品"
        mode="data"
        :show="true"
        class="empty-state"
      >
        <u-button type="primary" size="mini" @click="addItem"
          >添加物品</u-button
        >
      </u-empty>

      <u-loading
        v-else-if="loading"
        mode="circle"
        size="40"
        color="#4A90E2"
        vertical
        text="加载中..."
        class="loading-state"
      />

      <scroll-view
        v-else
        scroll-y
        enable-flex
        class="item-list"
        :style="{ height: listHeight + 'px' }"
      >
        <goodscard
          v-for="item in itemList"
          :key="item.id"
          :id="item.id"
          :item_code="item.item_code"
          :box_id="item.box_id"
          :auto_recognize_name="item.auto_recognize_name"
          :manual_edit_name="item.manual_edit_name"
          :item_tag="item.item_tag"
          :item_desc="item.item_desc"
          :put_in_time="item.put_in_time"
          :expire_time="item.expire_time"
          :is_valid="item.is_valid"
          :create_time="item.create_time"
          :update_time="item.update_time"
          @click="gotoItem(item.id)"
        />
      </scroll-view>

      <u-fab
        icon="plus"
        position="bottomRight"
        :offset="[40, 80]"
        @click="addItem"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import goodscard from "@/components/goodscard.vue";
import { getItems } from "@/api/items/itemsapi.js"; 

const spaceId = ref("");
const spaceName = ref("");
const itemList = ref([]);
const loading = ref(false);
const listHeight = ref(0);

onLoad((options) => {
  spaceId.value = options.id || "";
  spaceName.value = options.name || "收纳空间";
  loadData();
});

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync();
  const navHeight =
    (sysInfo.statusBarHeight || 0) + (sysInfo.platform === "ios" ? 44 : 48);
  listHeight.value = sysInfo.windowHeight - navHeight - 60;
});

const loadData = async () => {
  loading.value = true;
  try {
    // 根据收纳盒ID获取物品列表
    const res = await getItems({ box_id: spaceId.value });
    if (res && res.code === 200) {
      itemList.value = res.data || [];
    } else {
      itemList.value = [];
    }
  } catch (err) {
    console.error("加载物品失败:", err);
    uni.showToast({ title: "加载失败", icon: "none" });
    itemList.value = [];
  } finally {
    loading.value = false;
  }
};

const gotoItem = (id) => {
  uni.navigateTo({ url: `/pages/item/item?id=${id}` });
};

const addItem = () => {
  uni.navigateTo({ 
    url: `/pages/add-item/add-item?box_id=${spaceId.value}&box_name=${spaceName.value}`
  });
};
</script>

<style scoped>
.container {
  background-color: #f8f9fa;
  min-height: 100vh;
  position: relative;
}

.content {
  padding: 20rpx;
}

.item-list {
  padding: 20rpx;
  box-sizing: border-box;
}

.empty-state {
  margin-top: 200rpx;
}

.loading-state {
  margin-top: 200rpx;
}
</style>