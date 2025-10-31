<template>
  <view class="chat-page" :class="{ 'platform-container': true }">
    <!-- uview-plus 导航栏：增加返回按钮和沉浸式效果 -->
    <u-navbar 
      title="智能助手" 
      title-color="#333" 
      bg-color="#fff"
      border-bottom
      :left-icon="leftIcon"
      @left-click="handleBack"
      :custom-style="{ boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }"
    />

    <!-- 聊天区域：优化滚动体验 -->
    <scroll-view
      scroll-y
      class="chat-area"
      :scroll-top="scrollTop"
      :scroll-into-view="latestId"
      ref="chatScroll"
      :scroll-with-animation="true"
      @scroll="handleScroll"
    >
      <view class="chat-list">
        <!-- 系统提示消息（新增） -->
        <view class="system-tip" v-if="showSystemTip">
          <text class="tip-text">{{ systemTipText }}</text>
        </view>

        <!-- 聊天消息：增强uview-plus组件集成 -->
        <view
          v-for="msg in messages"
          :key="msg.id"
          :id="'msg-' + msg.id"
          class="msg-item"
          :class="{ 'msg-self': msg.isSelf, 'msg-ai': !msg.isSelf }"
          ref="messageItems"
        >
          <!-- uview-plus 头像：增加边框和hover效果 -->
          <u-avatar 
            :src="msg.avatar" 
            size="80"
            :custom-style="{ 
              border: '2px solid #f0f2f5',
              boxShadow: '0 2px 6px rgba(0,0,0,0.05)',
              transition: 'all 0.3s ease'
            }"
            @click="handleAvatarClick(msg)"
          />

          <view class="msg-content">
            <!-- 消息气泡：使用uview-plus色彩体系 -->
            <u-view 
              class="msg-text"
              :bg-color="msg.isSelf ? '#2979ff' : '#fff'"
              :color="msg.isSelf ? '#fff' : '#333'"
              :radius="'16rpx'"
              :padding="['20rpx', '28rpx']"
              :box-shadow="msg.isSelf ? '0 3rpx 12rpx rgba(41, 121, 255, 0.2)' : '0 3rpx 12rpx rgba(0, 0, 0, 0.06)'"
              :text-style="{ fontSize: '28rpx', lineHeight: '1.6' }"
            >
              {{ msg.text }}
            </u-view>

            <!-- 消息时间戳（新增） -->
            <text class="msg-time" :class="{ 'time-self': msg.isSelf }">
              {{ formatTime(msg.timestamp || Date.now()) }}
            </text>

            <!-- 推荐物品：使用uview-plus卡片组件 -->
            <view v-if="msg.items" class="recommend-items" ref="recommendItems">
              <u-card 
                v-for="(item, idx) in msg.items"
                :key="item.id"
                class="recommend-item"
                @click="viewItem(item, $event)"
                :border="false"
                :shadow="true"
                :radius="'12rpx'"
                :padding="'15rpx'"
                :custom-style="{ 
                  marginRight: '20rpx', 
                  minWidth: '140rpx',
                  transition: 'all 0.3s ease',
                  cursor: 'pointer'
                }"
              >
                <!-- uview-plus 图片：增加加载动画和错误占位 -->
                <u-image
                  :src="item.image"
                  width="120rpx"
                  height="120rpx"
                  radius="'8rpx'"
                  :lazy-load="true"
                  :show-loading="true"
                  :loading-icon="loadingIcon"
                  :error-icon="errorIcon"
                  :custom-style="{ objectFit: 'cover' }"
                />
                <!-- uview-plus 文本：自动省略 -->
                <u-text 
                  class="item-name"
                  :max-lines="1"
                  :text-style="{ 
                    fontSize: '24rpx', 
                    marginTop: '10rpx',
                    color: '#666'
                  }"
                >
                  {{ item.itemName }}
                </u-text>
              </u-card>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区：优化uview-plus组件交互 -->
    <view class="input-area" :class="{ 'safe-area-bottom': true }" ref="inputArea">
      <!-- uview-plus 输入框：增强样式和功能 -->
      <u-input
        v-model="inputText"
        placeholder="请输入..."
        :focus="inputFocus"
        confirm-type="send"
        @confirm="sendMessage"
        ref="inputField"
        :border="false"
        :radius="'40rpx'"
        :bg-color="'#f5f7fa'"
        :padding="['20rpx', '30rpx']"
        :height="'88rpx'"
        :placeholder-style="{ color: '#999', fontSize: '26rpx' }"
        :input-style="{ fontSize: '28rpx', color: '#333' }"
        @focus="handleInputFocus"
        @blur="handleInputBlur"
      >
        <!-- 语音按钮：根据平台适配 -->
        <template #suffix>
          <!-- #ifdef MP-WEIXIN -->
          <u-button
            class="voice-btn"
            @longpress="onVoiceStart"
            @touchend="onVoiceStop"
            ref="voiceBtn"
            shape="circle"
            size="mini"
            :bg-color="isRecording ? 'rgba(41, 121, 255, 0.2)' : 'transparent'"
            :icon="isRecording ? 'microphone-filled' : 'microphone'"
            :icon-size="48rpx"
            :icon-color="#2979ff"
            :custom-style="{ padding: '15rpx' }"
          />
          <!-- #endif -->

          <!-- #ifndef MP-WEIXIN -->
          <u-button
            class="voice-btn"
            @click="toggleVoice"
            ref="voiceBtn"
            shape="circle"
            size="mini"
            bg-color="transparent"
            :icon="showVoice ? 'microphone-filled' : 'microphone'"
            :icon-size="'48rpx'"
            :icon-color="'#2979ff'"
            :custom-style="{ padding: '15rpx' }"
          />
          <!-- #endif -->
        </template>
      </u-input>

      <!-- 语音录制弹窗（新增） -->
      <u-popup 
        v-model="showVoicePopup"
        mode="center"
        :mask-opacity="0.6"
        :border-radius="'20rpx'"
        :custom-style="{ width: '300rpx', height: '300rpx', background: 'rgba(0,0,0,0.8)' }"
      >
        <view class="voice-popup-content">
          <u-icon 
            :name="isRecording ? 'microphone-filled' : 'microphone'"
            size="80rpx"
            color="#fff"
            ref="voicePopupIcon"
          />
          <u-text 
            :text-style="{ 
              color: '#fff', 
              fontSize: '28rpx', 
              marginTop: '20rpx' 
            }"
          >
            {{ isRecording ? '正在录音...' : '点击开始录音' }}
          </u-text>
        </view>
      </u-popup>
    </view>

    <!-- 录音中提示（新增） -->
    <u-toast 
      v-model="showRecordingToast"
      :icon="recordingIcon"
      :text="recordingText"
      :bg-color="recordingBg"
      :text-color="'#fff'"
      position="bottom"
      :custom-style="{ padding: '15rpx 20rpx', borderRadius: '10rpx' }"
    />
  </view>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from "vue";
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";

import http from "@/utils/request";
import { formatTime } from "@/utils/date"; // 假设已有时间格式化工具

// 注册GSAP插件
gsap.registerPlugin(ScrollTrigger);

// 1. 静态资源配置（uview-plus图标）
const leftIcon = ref({ name: 'arrow-left', size: 36, color: '#333' });
const loadingIcon = ref(UIconLoading);
const errorIcon = ref(UIconErrorCircle);
const recordingIcon = ref(UIconMicrophoneFilled);

// 2. 状态管理
const messages = ref([
  {
    id: 1,
    text: "你好！我是收纳助手，可以帮你查找物品。",
    isSelf: false,
    avatar: "/static/avatar/ai.png",
    timestamp: Date.now() - 30000
  },
  {
    id: 2,
    text: "我想找我的钢笔",
    isSelf: true,
    avatar: "/static/avatar/user.png",
    timestamp: Date.now() - 25000
  },
]);
const inputText = ref("");
const inputFocus = ref(false);
const showVoice = ref(false);
const showVoicePopup = ref(false);
const showRecordingToast = ref(false);
const showSystemTip = ref(true);
const systemTipText = ref("可发送物品名称快速查找");
const latestId = ref("msg-2");
const scrollTop = ref(0);
const isRecording = ref(false);
const recordingText = ref("正在录音...");
const recordingBg = ref("rgba(41, 121, 255, 0.8)");

// 3. DOM引用
const chatScroll = ref(null);
const messageItems = ref([]);
const recommendItems = ref(null);
const inputArea = ref(null);
const inputField = ref(null);
const voiceBtn = ref(null);
const voicePopupIcon = ref(null);

// 4. 初始化动画（增强版）
onMounted(() => {
  // 初始消息入场动画
  const initialMessages = messageItems.value;
  gsap.set(initialMessages, {
    opacity: 0,
    y: 20,
    scale: 0.95
  });
  
  // 分批次入场（AI消息和用户消息错开）
  const aiMessages = initialMessages.filter(item => item.classList.contains('msg-ai'));
  const selfMessages = initialMessages.filter(item => item.classList.contains('msg-self'));
  
  gsap.to(aiMessages, {
    opacity: 1,
    y: 0,
    scale: 1,
    duration: 0.5,
    stagger: 0.3,
    ease: "power2.out"
  });
  
  gsap.to(selfMessages, {
    opacity: 1,
    y: 0,
    scale: 1,
    duration: 0.5,
    stagger: 0.3,
    delay: 0.2,
    ease: "power2.out"
  });

  // 输入区域入场动画
  gsap.from(inputArea.value, {
    y: 50,
    opacity: 0,
    duration: 0.6,
    delay: 0.5,
    ease: "power2.out"
  });

  // 系统提示淡入动画
  gsap.from('.system-tip', {
    opacity: 0,
    scale: 0.9,
    duration: 0.5,
    delay: 0.8,
    ease: "power2.out"
  });

  // 滚动区域动画监听
  if (chatScroll.value) {
    ScrollTrigger.create({
      trigger: chatScroll.value,
      start: "top top",
      end: "bottom bottom",
      onLeaveBack: () => {
        // 顶部回弹动画
        gsap.to(chatScroll.value, {
          scrollTop: 10,
          duration: 0.3,
          ease: "power1.out"
        });
      }
    });
  }
});

// 5. 录音相关逻辑（增强反馈）
const onVoiceStart = () => {
  isRecording.value = true;
  showVoicePopup.value = true;
  showRecordingToast.value = true;
  
  // 录音按钮脉冲动画
  if (voiceBtn.value) {
    gsap.to(voiceBtn.value, {
      scale: 1.2,
      duration: 0.3,
      ease: "power1.out"
    });
    
    // 弹窗图标呼吸动画
    if (voicePopupIcon.value) {
      gsap.to(voicePopupIcon.value, {
        scale: 1.2,
        opacity: 0.8,
        duration: 0.8,
        yoyo: true,
        repeat: -1,
        ease: "sine.inOut"
      });
    }
  }
};

const onVoiceStop = () => {
  isRecording.value = false;
  showVoicePopup.value = false;
  showRecordingToast.value = false;
  
  // 停止动画
  if (voiceBtn.value) {
    gsap.killTweensOf(voiceBtn.value);
    gsap.to(voiceBtn.value, {
      scale: 1,
      duration: 0.3,
      ease: "power1.out"
    });
  }
  if (voicePopupIcon.value) {
    gsap.killTweensOf(voicePopupIcon.value);
  }
};

// 6. 发送消息处理（增强动画）
const sendMessage = async () => {
  const content = inputText.value.trim();
  if (!content) return;

  const msgId = Date.now();
  // 添加用户消息
  messages.value.push({
    id: msgId,
    text: content,
    isSelf: true,
    avatar: "/static/avatar/user.png",
    timestamp: msgId
  });

  // 滚动到最新消息
  latestId.value = "msg-" + msgId;
  inputText.value = "";

  // 用户消息动画（右侧滑入）
  await nextTick();
  const newMsg = messageItems.value[messageItems.value.length - 1];
  gsap.from(newMsg, {
    opacity: 0,
    x: 80,
    scale: 0.92,
    duration: 0.4,
    ease: "power2.out"
  });

  try {
    // 显示"正在思考"提示
    showSystemTip.value = true;
    systemTipText.value = "正在思考...";
    await new Promise(resolve => setTimeout(resolve, 800));
    
    const data = await http.post('/chat', { message: content });
    // 隐藏提示
    showSystemTip.value = false;
    
    // 添加AI回复（兼容后端返回 {reply, items} 或 data 层）
    const reply = data.reply ?? data?.data?.reply ?? '已收到';
    const items = data.items ?? data?.data?.items ?? [];
    const aiMsgId = Date.now();
    messages.value.push({
      id: aiMsgId,
      text: reply,
      items,
      isSelf: false,
      avatar: "/static/avatar/ai.png",
      timestamp: aiMsgId
    });

      // AI消息动画（左侧滑入）
      await nextTick();
      const aiMsg = messageItems.value[messageItems.value.length - 1];
      gsap.from(aiMsg, {
        opacity: 0,
        x: -80,
        scale: 0.92,
        duration: 0.4,
        ease: "power2.out"
      });

      // 推荐物品动画（弹性入场）
      if (res.data.items && res.data.items.length) {
        await nextTick();
        const items = recommendItems.value?.querySelectorAll('.recommend-item');
        if (items) {
          gsap.set(items, {
            opacity: 0,
            y: 30,
            scale: 0.85
          });
          
          gsap.to(items, {
            opacity: 1,
            y: 0,
            scale: 1,
            duration: 0.5,
            stagger: 0.18,
            delay: 0.2,
            ease: "back.out(1.4)"
          });
        }
      }

      latestId.value = "msg-" + aiMsgId;
    } else {
      // 错误提示动画
      showSystemTip.value = true;
      systemTipText.value = "发送失败，请重试";
      gsap.from('.system-tip', {
        x: 20,
        duration: 0.2,
        yoyo: true,
        repeat: 2,
        ease: "power1.inOut"
      });
    }
  } catch (e) {
    console.error(e);
    showSystemTip.value = true;
    systemTipText.value = "发送失败，请重试";
    gsap.from('.system-tip', {
      x: 20,
      duration: 0.2,
      yoyo: true,
      repeat: 2,
      ease: "power1.inOut"
    });
  }
};

// 7. 语音按钮切换（增强动画）
const toggleVoice = () => {
  showVoice.value = !showVoice.value;
  showVoicePopup.value = showVoice.value;
  
  if (voiceBtn.value && voicePopupIcon.value) {
    // 按钮旋转动画
    gsap.from(voiceBtn.value, {
      rotation: 30,
      scale: 1.3,
      duration: 0.3,
      ease: "back.out(1.5)"
    });
    // 弹窗淡入
    gsap.from(voicePopupIcon.value, {
      scale: 0.8,
      opacity: 0,
      duration: 0.4,
      ease: "back.out(1.2)"
    });
  }
};

// 8. 查看物品（增强反馈）
const viewItem = (item, event) => {
  const activeItem = event.currentTarget;
  // 点击缩放反馈
  gsap.to(activeItem, {
    scale: 0.93,
    duration: 0.15,
    yoyo: true,
    repeat: 1,
    ease: "power1.inOut"
  });
  
  // 物品详情跳转（可替换为路由跳转）
  console.log("查看物品:", item);
  uni.showToast({
    title: `查看${item.itemName}详情`,
    icon: "none",
    duration: 1500
  });
};

// 9. 辅助交互函数
const handleBack = () => {
  // 返回上一页动画
  gsap.to(chatScroll.value, {
    opacity: 0,
    duration: 0.3,
    ease: "power1.out",
    onComplete: () => {
      uni.navigateBack();
    }
  });
};

const handleAvatarClick = (msg) => {
  // 头像点击动画
  const avatar = event.currentTarget;
  gsap.to(avatar, {
    scale: 1.1,
    duration: 0.2,
    yoyo: true,
    repeat: 1,
    ease: "power1.inOut"
  });
  
  // 显示用户信息（示例）
  const tip = msg.isSelf ? "这是你" : "智能助手";
  uni.showToast({ title: tip, icon: "none" });
};

const handleInputFocus = () => {
  // 输入框聚焦动画
  gsap.to(inputField.value.$el, {
    scale: 1.02,
    bgColor: "#fff",
    boxShadow: "0 4rpx 16rpx rgba(0,0,0,0.08)",
    duration: 0.3,
    ease: "power1.out"
  });
};

const handleInputBlur = () => {
  // 输入框失焦动画
  gsap.to(inputField.value.$el, {
    scale: 1,
    bgColor: "#f5f7fa",
    boxShadow: "none",
    duration: 0.3,
    ease: "power1.out"
  });
};

const handleScroll = () => {
  // 滚动时隐藏系统提示
  showSystemTip.value = false;
};

// 10. 监听录音状态
watch(isRecording, (newVal) => {
  if (newVal) {
    recordingText.value = "正在录音...";
    recordingBg.value = "rgba(41, 121, 255, 0.8)";
  } else {
    recordingText.value = "录音已结束";
    recordingBg.value = "rgba(67, 191, 86, 0.8)";
    // 录音结束提示延时消失
    setTimeout(() => {
      showRecordingToast.value = false;
    }, 1500);
  }
});
</script>

<style scoped>
/* 基础布局优化 */
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 聊天区域优化 */
.chat-area {
  flex: 1;
  padding: 20rpx;
  overflow: hidden;
}

.chat-list {
  min-height: 100%;
  padding-bottom: 20rpx;
}

/* 系统提示样式 */
.system-tip {
  display: flex;
  justify-content: center;
  margin: 20rpx 0;
}

.tip-text {
  padding: 8rpx 20rpx;
  background-color: rgba(0,0,0,0.05);
  color: #999;
  font-size: 24rpx;
  border-radius: 20rpx;
}

/* 消息项布局 */
.msg-item {
  display: flex;
  margin: 30rpx 0;
  padding: 0 10rpx;
  position: relative;
  align-items: flex-start;
}

.msg-self {
  flex-direction: row-reverse;
}

/* 消息内容容器 */
.msg-content {
  max-width: 72%;
  margin: 0 20rpx;
  display: flex;
  flex-direction: column;
}

/* 消息时间戳 */
.msg-time {
  font-size: 22rpx;
  color: #999;
  margin-top: 8rpx;
  margin-left: 12rpx;
  display: inline-block;
}

.time-self {
  margin-left: 0;
  margin-right: 12rpx;
  text-align: right;
}

/* 推荐物品容器 */
.recommend-items {
  display: flex;
  margin-top: 20rpx;
  padding: 10rpx 0;
  overflow-x: auto;
  scrollbar-width: none;
  gap: 15rpx;
}

.recommend-items::-webkit-scrollbar {
  display: none;
}

/* 输入区域优化 */
.input-area {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  background-color: #fff;
  border-top: 1px solid #f0f2f5;
}

/* 语音弹窗内容 */
.voice-popup-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

/* 安全区域适配 */
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

/* 适配暗黑模式（可选） */
@media (prefers-color-scheme: dark) {
  .chat-page {
    background-color: #1a1a1a;
  }
  
  .msg-ai .msg-text {
    background: #2d2d2d;
    color: #eee;
  }
  
  .u-input {
    background-color: #2d2d2d !important;
  }
  
  .input-area {
    background-color: #1a1a1a;
    border-top-color: #2d2d2d;
  }
}
</style>