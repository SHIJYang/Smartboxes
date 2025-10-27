<template>
  <view class="chat-page" :class="{ 'platform-container': true }">
    <u-navbar title="智能助手" />

    <!-- 聊天区域 -->
    <scroll-view
      scroll-y
      class="chat-area"
      :scroll-top="scrollTop"
      :scroll-into-view="latestId"
    >
      <view class="chat-list">
        <view
          v-for="msg in messages"
          :key="msg.id"
          :id="'msg-' + msg.id"
          class="msg-item"
          :class="{ 'msg-self': msg.isSelf }"
        >
          <u-avatar :src="msg.avatar" size="80" />
          <view class="msg-content">
            <text class="msg-text">{{ msg.text }}</text>
            <!-- 推荐物品 -->
            <view v-if="msg.items" class="recommend-items">
              <view
                v-for="item in msg.items"
                :key="item.id"
                class="recommend-item"
                @click="viewItem(item)"
              >
                <u-image
                  :src="item.image"
                  width="120rpx"
                  height="120rpx"
                  radius="6"
                />
                <text class="item-name">{{ item.itemName }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区根据平台调整 -->
    <view class="input-area" :class="{ 'safe-area-bottom': true }">
      <!-- #ifdef MP-WEIXIN -->
      <u-input
        v-model="inputText"
        placeholder="请输入..."
        :focus="inputFocus"
        confirm-type="send"
        @confirm="sendMessage"
      >
        <template #suffix>
          <button
            class="voice-btn mp-hover"
            @longpress="onVoiceStart"
            @touchend="onVoiceStop"
          >
            <u-icon name="mic" size="48rpx" />
          </button>
        </template>
      </u-input>
      <!-- #endif -->

      <!-- #ifndef MP-WEIXIN -->
      <u-input
        v-model="inputText"
        placeholder="请输入..."
        :focus="inputFocus"
        @confirm="sendMessage"
      >
        <template #suffix>
          <u-icon name="mic" size="48rpx" @click="toggleVoice" />
        </template>
      </u-input>
      <!-- #endif -->
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
// import http from "@/utils/request";
import { postChat } from "../../mock/api";
import { platform } from "@/utils/platform";

const messages = ref([
  {
    id: 1,
    text: "你好！我是收纳助手，可以帮你查找物品。",
    isSelf: false,
    avatar: "/static/avatar/ai.png",
  },
  {
    id: 2,
    text: "我想找我的钢笔",
    isSelf: true,
    avatar: "/static/avatar/user.png",
  },
]);

const inputText = ref("");
const inputFocus = ref(false);

// 补充避免未定义
const showVoice = ref(false);
const latestId = ref("");
const scrollTop = ref(0);

const onVoiceStart = () => {
  console.log("开始录音");
  // 实际项目中调用 uni.startRecord()
};

const onVoiceStop = () => {
  console.log("结束录音");
  // 调用 uni.stopRecord() 并上传识别
};

const sendMessage = async () => {
  const content = inputText.value.trim();
  if (!content) return;

  const msgId = Date.now();
  messages.value.push({
    id: msgId,
    text: content,
    isSelf: true,
    avatar: "/static/avatar/user.png",
  });

  latestId.value = "msg-" + msgId;
  inputText.value = "";

  try {
    const res = await postChat(content);
    if (res && res.code === 200) {
      messages.value.push({
        id: Date.now(),
        text: res.data.reply,
        items: res.data.items,
        isSelf: false,
        avatar: "/static/avatar/ai.png",
      });
    } else {
      uni.showToast({ title: "发送失败", icon: "error" });
    }
  } catch (e) {
    uni.showToast({ title: "发送失败", icon: "error" });
  }
};

const toggleVoice = () => {
  showVoice.value = true;
  // TODO: 调用语音识别API
  setTimeout(() => {
    showVoice.value = false;
  }, 2000);
};
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.chat-area {
  flex: 1;
  padding: 20rpx;
  background-color: #f5f5f5;
}
.input-area {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff;
  border-top: 1px solid #eee;
}
.msg-item {
  display: flex;
  margin: 30rpx 0;
  padding: 0 20rpx;
}
.msg-self {
  flex-direction: row-reverse;
}
.msg-content {
  max-width: 70%;
  margin: 0 20rpx;
}
.msg-text {
  padding: 20rpx;
  border-radius: 12rpx;
  background: #fff;
  font-size: 28rpx;
}
.msg-self .msg-text {
  background: #2979ff;
  color: #fff;
}
.recommend-items {
  display: flex;
  margin-top: 20rpx;
  overflow-x: auto;
}
.recommend-item {
  margin-right: 20rpx;
  text-align: center;
}
.voice-popup {
  width: 300rpx;
  height: 300rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 20rpx;
}
.voice-btn {
  background: none;
  border: none;
  padding: 0;
  line-height: 1;
}
</style>
