// stores/chatStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { AiChatRequestDTO } from '@/common/types';

interface ChatMessage {
    id: number;
    role: 'user' | 'assistant';
    content: string;
    timestamp: string;
    action?: string; // e.g., 'OPEN_BOX_001'
}

export const useChatStore = defineStore('chat', {
  state: () => ({
    messages: [] as ChatMessage[],
    loading: false,     // 发送中
    isThinking: false,  // AI 思考中 (可用于显示 Loading 动画)
  }),

  getters: {
    lastMessage: (state) => state.messages[state.messages.length - 1],
    isEmpty: (state) => state.messages.length === 0,
    // 配合 chat.vue 判断是否为空聊天，用于显示欢迎语
    isEmptyChat: (state) => state.messages.length === 0
  },

  actions: {
    async sendMessage(text: string, userId: number) {
      if (!text.trim() || this.loading) return;

      // 1. 添加用户消息
      const userMsg: ChatMessage = {
        id: Date.now(),
        role: 'user',
        content: text,
        timestamp: new Date().toISOString()
      };
      this.messages.push(userMsg);
      
      this.loading = true;
      this.isThinking = true;

      try {
        const req: AiChatRequestDTO = { userId, message: text };
        const res = await API.sendChat(req);

        if (res.code === 200) {
            // 2. 添加 AI 响应
            const aiMsg: ChatMessage = {
                id: Date.now() + 1,
                role: 'assistant',
                content: res.data.reply,
                action: res.data.action, // 可能包含 'OPEN_BOX_xxx' 动作
                timestamp: new Date().toISOString()
            };
            this.messages.push(aiMsg);
            
            // 3. 处理自动动作 (如跳转盒子)
            if (res.data.action) {
                this.handleAiAction(res.data.action);
            }
        } else {
            this.appendSystemMessage(`错误: ${res.msg}`);
        }
      } catch (error) {
        console.error(error);
        this.appendSystemMessage('网络连接失败，请稍后重试。');
      } finally {
        this.loading = false;
        this.isThinking = false;
      }
    },

    appendSystemMessage(content: string) {
        this.messages.push({
            id: Date.now(),
            role: 'assistant',
            content: `[系统] ${content}`,
            timestamp: new Date().toISOString()
        });
    },

    handleAiAction(action: string) {
        console.log('AI Triggered Action:', action);
        // 这里可以配合 EventBus 或 UniApp 的全局事件来触发路由跳转
        // 例如：uni.$emit('AI_ACTION', action);
    },

    clearMessages() {
        this.messages = [];
    }
  }
});