// stores/boxStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { BoxDTO, QueryBoxDTO } from '@/common/types';

export const useBoxStore = defineStore('box', {
  state: () => ({
    boxList: [] as BoxDTO[],
    currentBox: null as BoxDTO | null,
    pagination: {
      page: 1,
      size: 10,
      total: 0
    },
    // 查询条件持久化
    queryParams: {
      userId: undefined as number | undefined,
      boxCode: '',
      boxName: '',
      status: undefined as number | undefined
    } as QueryBoxDTO,
    
    loading: false,
    detailLoading: false
  }),

  getters: {
    onlineBoxes: (state) => state.boxList.filter(box => box.status === 1),
    offlineBoxes: (state) => state.boxList.filter(box => box.status === 0),
    mainBoxes: (state) => state.boxList.filter(box => box.boxType === 1),
  },

  actions: {
    // 1. 获取盒子列表 (分页)
    async fetchBoxList(params: QueryBoxDTO = {}) {
      this.loading = true;
      // 合并查询参数
      this.queryParams = { ...this.queryParams, ...params };

      try {
        const res = await API.getBoxPage({ 
            ...this.queryParams, 
            page: this.pagination.page, 
            size: this.pagination.size 
        });

        if (res.code === 200 && res.data) {
          this.boxList = res.data.data;
          this.pagination = {
            page: res.data.page,
            size: res.data.size,
            total: res.data.total
          };
        }
        return this.boxList;
      } catch (error) {
        console.error('Fetch box list error:', error);
      } finally {
        this.loading = false;
      }
    },

    // 2. 获取详情 (智能回退策略)
    // 因 OpenAPI 缺少 /api/boxes/{id}，优先查本地，本地没有则尝试查列表
    async fetchBoxDetail(id: number) {
      this.detailLoading = true;
      
      // A. 优先查本地缓存
      const localBox = this.boxList.find(b => b.id === id);
      if (localBox) {
        this.currentBox = { ...localBox };
        this.detailLoading = false;
        return;
      }

      // B. 尝试调用 Mock/兼容接口 (如果后端支持)
      try {
        const res = await API.getBoxDetailMock(id);
        if (res.code === 200) {
          this.currentBox = res.data;
          // 同步到列表
          this._upsertList(res.data);
          return;
        }
      } catch (e) {
        console.warn('API getBoxDetailMock failed, trying fallback...');
      }

      // C. 回退策略：重新拉取列表 (可能需要扩大搜索范围)
      // 注意：这里假设用户盒子数量不多，或者ID能在当前查询条件下找到
      try {
        await this.fetchBoxList({ size: 100 }); // 尝试拉取更多
        const found = this.boxList.find(b => b.id === id);
        if (found) {
            this.currentBox = { ...found };
        } else {
            console.error('Box not found in list');
            this.currentBox = null;
        }
      } finally {
        this.detailLoading = false;
      }
    },

    // 3. 保存 (新增/修改)
    async saveBox(boxData: BoxDTO) {
      this.loading = true;
      try {
        let res;
        if (boxData.id) {
          res = await API.updateBox(boxData);
        } else {
          res = await API.addBox(boxData);
        }

        if (res.code === 200) {
          await this.fetchBoxList(); // 刷新列表
          return { success: true };
        }
        return { success: false, message: res.msg };
      } catch (e) {
        return { success: false, message: '操作失败' };
      } finally {
        this.loading = false;
      }
    },

    // 4. 删除
    async deleteBox(id: number) {
      try {
        const res = await API.deleteBox(id);
        if (res.code === 200) {
          this.boxList = this.boxList.filter(b => b.id !== id);
          if (this.currentBox?.id === id) this.currentBox = null;
          this.pagination.total = Math.max(0, this.pagination.total - 1);
          return { success: true };
        }
        return { success: false, message: res.msg };
      } catch (e) {
        return { success: false, message: '删除失败' };
      }
    },
    
    // 内部辅助：更新或插入列表
    _upsertList(box: BoxDTO) {
        const idx = this.boxList.findIndex(b => b.id === box.id);
        if (idx !== -1) {
            this.boxList[idx] = box;
        } else {
            this.boxList.push(box);
        }
    }
  }
});