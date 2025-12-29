// stores/index.ts
import { useUserStore } from './userStore';
import { useBoxStore } from './boxStore';
import { useItemStore } from './itemStore';
import { useChatStore } from './chatStore';

// 导出单独的 Store
export { useUserStore } from './userStore';
export { useBoxStore } from './boxStore';
export { useItemStore } from './itemStore';
export { useChatStore } from './chatStore';

/**
 * 组合所有store的便捷Hook
 * Usage: const { userStore, boxStore } = useStores();
 */
export const useStores = () => {
  return {
    userStore: useUserStore(),
    boxStore: useBoxStore(),
    itemStore: useItemStore(),
    chatStore: useChatStore()
  };
};