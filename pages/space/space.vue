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
          :name="item.name"
          :category="item.category"
          :description="item.description"
          :quantity="item.quantity"
          :location="item.location"
          :last-modified="item.lastModified"
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
import { ref, onMounted, computed } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import goodscard from "@/components/goodscard.vue";

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
    // 模拟数据加载
    await new Promise((resolve) => setTimeout(resolve, 1000));
    itemList.value = [
      {
        id: 1,
        name: "螺丝刀套装",
        category: "工具",
        description: "多功能家用螺丝刀",
        quantity: 1,
        location: "工具箱",
        lastModified: "2024-01-15",
      },
      {
        id: 2,
        name: "扳手",
        category: "工具",
        description: "10mm开口扳手",
        quantity: 2,
        location: "工具箱",
        lastModified: "2024-01-10",
      },
      {
        id: 3,
        name: "钳子",
        category: "工具",
        description: "尖嘴钳",
        quantity: 1,
        location: "工具箱",
        lastModified: "2024-01-20",
      },
    ];
  } catch (err) {
    uni.showToast({ title: "加载失败", icon: "none" });
  } finally {
    loading.value = false;
  }
};

const gotoItem = (id) => {
  uni.navigateTo({ url: `/pages/item/item?id=${id}` });
};

const addItem = () => {
  uni.navigateTo({ url: "/pages/add-item/add-item" });
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
