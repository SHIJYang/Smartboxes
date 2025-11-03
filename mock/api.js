// 1. 固定Mock数据源（集中管理）
const MOCK_DATA = {
  // 固定用户信息
  USER_INFO: {
    code: 200,
    data: {
      id: "user_maizhi_001",
      nickname: "迈植默认用户",
      avatar: "https://picsum.photos/100/100?random=1",
      stats: { boxCount: 5, itemCount: 32, categoryCount: 8 }
    }
  },

  // 固定物品列表（18条，支持分页）
  ITEMS: [
    { id: 1, itemName: "黑色钢笔", description: "办公用黑色钢笔 墨水已装满", image: "https://picsum.photos/200/200?random=101", boxName: "1号收纳盒", categoryId: 1, createTime: "2024-01-15T08:30:00.000Z" },
    { id: 2, itemName: "无线耳机", description: "白色无线耳机 续航约4小时", image: "https://picsum.photos/200/200?random=102", boxName: "2号收纳盒", categoryId: 2, createTime: "2024-01-16T14:20:00.000Z" },
    { id: 3, itemName: "笔记本", description: "A5规格 空白内页笔记本", image: "https://picsum.photos/200/200?random=103", boxName: "1号收纳盒", categoryId: 1, createTime: "2024-01-17T09:15:00.000Z" },
    { id: 4, itemName: "手机充电器", description: "Type-C接口 20W快充", image: "https://picsum.photos/200/200?random=104", boxName: "3号收纳盒", categoryId: 3, createTime: "2024-01-18T11:40:00.000Z" },
    { id: 5, itemName: "保温杯", description: "500ml 不锈钢保温杯 黑色", image: "https://picsum.photos/200/200?random=105", boxName: "4号收纳盒", categoryId: 4, createTime: "2024-01-19T15:25:00.000Z" },
    { id: 6, itemName: "钥匙串", description: "带挂扣 含3把钥匙", image: "https://picsum.photos/200/200?random=106", boxName: "2号收纳盒", categoryId: 5, createTime: "2024-01-20T08:50:00.000Z" },
    { id: 7, itemName: "铅笔", description: "HB铅笔 10支装 带橡皮", image: "https://picsum.photos/200/200?random=107", boxName: "1号收纳盒", categoryId: 1, createTime: "2024-01-21T10:10:00.000Z" },
    { id: 8, itemName: "U盘", description: "64GB USB3.0 金属外壳", image: "https://picsum.photos/200/200?random=108", boxName: "3号收纳盒", categoryId: 3, createTime: "2024-01-22T13:30:00.000Z" },
    { id: 9, itemName: "运动手表", description: "可测心率 防水50米", image: "https://picsum.photos/200/200?random=109", boxName: "4号收纳盒", categoryId: 4, createTime: "2024-01-23T16:45:00.000Z" },
    { id: 10, itemName: "钱包", description: "短款 黑色皮质 多卡槽", image: "https://picsum.photos/200/200?random=110", boxName: "2号收纳盒", categoryId: 5, createTime: "2024-01-24T09:20:00.000Z" },
    { id: 11, itemName: "便利贴", description: "黄色 100张/本 可粘贴", image: "https://picsum.photos/200/200?random=111", boxName: "1号收纳盒", categoryId: 1, createTime: "2024-01-25T11:55:00.000Z" },
    { id: 12, itemName: "数据线", description: "Type-C to Lightning 1米长", image: "https://picsum.photos/200/200?random=112", boxName: "3号收纳盒", categoryId: 3, createTime: "2024-01-26T14:10:00.000Z" },
    { id: 13, itemName: "墨镜", description: "偏光镜片 黑色镜框", image: "https://picsum.photos/200/200?random=113", boxName: "4号收纳盒", categoryId: 4, createTime: "2024-01-27T08:35:00.000Z" },
    { id: 14, itemName: "指甲刀", description: "不锈钢 带锉刀", image: "https://picsum.photos/200/200?random=114", boxName: "5号收纳盒", categoryId: 5, createTime: "2024-01-28T10:50:00.000Z" },
    { id: 15, itemName: "书签", description: "金属材质 刻字款", image: "https://picsum.photos/200/200?random=115", boxName: "1号收纳盒", categoryId: 1, createTime: "2024-01-29T13:20:00.000Z" },
    { id: 16, itemName: "移动电源", description: "10000mAh 双向快充", image: "https://picsum.photos/200/200?random=116", boxName: "3号收纳盒", categoryId: 3, createTime: "2024-01-30T15:45:00.000Z" },
    { id: 17, itemName: "围巾", description: "羊毛材质 灰色 保暖", image: "https://picsum.photos/200/200?random=117", boxName: "4号收纳盒", categoryId: 4, createTime: "2024-02-01T09:10:00.000Z" },
    { id: 18, itemName: "剪刀", description: "办公用 圆头 安全款", image: "https://picsum.photos/200/200?random=118", boxName: "5号收纳盒", categoryId: 5, createTime: "2024-02-02T11:30:00.000Z" }
  ],

  // 固定收纳盒列表（5条）
  // 固定收纳盒列表（5条）
  BOXES: {
    code: 200,
    data: [
      { 
        id: 1, 
        boxCode: "BOX8A2C4E", 
        name: "1号收纳盒", 
        category: "box",
        itemCount: 8,
        location: "书房",
        lastModified: "2024-02-03T16:20:00.000Z",
        batteryLevel: 92,
        isCharging: false,
        status: 1, 
        rssi: -45, 
        lastHeartbeatTime: "2024-02-03T16:20:00.000Z" 
      },
      { 
        id: 2, 
        boxCode: "BOX3D5F7A", 
        name: "2号收纳盒", 
        category: "drawer",
        itemCount: 6,
        location: "卧室",
        lastModified: "2024-02-03T16:22:00.000Z",
        batteryLevel: 85,
        isCharging: true,
        status: 1, 
        rssi: -55, 
        lastHeartbeatTime: "2024-02-03T16:22:00.000Z" 
      },
      { 
        id: 3, 
        boxCode: "BOX9B1D3C", 
        name: "3号收纳盒", 
        category: "shelf",
        itemCount: 5,
        location: "客厅",
        lastModified: "2024-02-03T16:21:00.000Z",
        batteryLevel: 78,
        isCharging: false,
        status: 1, 
        rssi: -65, 
        lastHeartbeatTime: "2024-02-03T16:21:00.000Z" 
      },
      { 
        id: 4, 
        boxCode: "BOX2E4G6H", 
        name: "4号收纳盒", 
        category: "wardrobe",
        itemCount: 7,
        location: "衣帽间",
        lastModified: "2024-02-03T15:50:00.000Z",
        batteryLevel: 32,
        isCharging: false,
        status: 0, 
        rssi: -75, 
        lastHeartbeatTime: "2024-02-03T15:50:00.000Z" 
      },
      { 
        id: 5, 
        boxCode: "BOX5H7J9K", 
        name: "5号收纳盒", 
        category: "cabinet",
        itemCount: 6,
        location: "厨房",
        lastModified: "2024-02-03T16:18:00.000Z",
        batteryLevel: 65,
        isCharging: true,
        status: 1, 
        rssi: -50, 
        lastHeartbeatTime: "2024-02-03T16:18:00.000Z" 
      }
    ]
  },

  // 固定聊天推荐物品（与ITEMS数据对应）
  CHAT_RECOMMEND_ITEMS: [
    { id: "item_rec_001", itemName: "黑色钢笔", boxName: "1号收纳盒", image: "https://picsum.photos/120/120?random=101" },
    { id: "item_rec_002", itemName: "手机充电器", boxName: "3号收纳盒", image: "https://picsum.photos/120/120?random=104" }
  ]
};

// 2. Mock工具函数（整合所有方法）
const mockUtils = {
  // 获取用户信息
  getUserInfo: () => MOCK_DATA.USER_INFO,

  // 获取物品列表（支持分页）
  getItems: (params = {}) => {
    const page = Number(params.page || 1);
    const pageSize = 10;
    const start = (page - 1) * pageSize;
    const pageData = MOCK_DATA.ITEMS.slice(start, start + pageSize);
    return { code: 200, data: pageData, total: MOCK_DATA.ITEMS.length };
  },

  // 获取收纳盒列表
  getBoxes: () => MOCK_DATA.BOXES,

  // 聊天回复
  postChat: (message = "") => ({
    code: 200,
    data: {
      reply: `已收到：「${String(message).slice(0, 20)}」，为您推荐相关物品：黑色钢笔、手机充电器`,
      items: MOCK_DATA.CHAT_RECOMMEND_ITEMS
    }
  }),

  // fetch拦截器初始化（仅开发环境生效）
  setupMock: () => {
    if (typeof window === 'undefined' || process.env.NODE_ENV !== 'development') return;

    try {
      const originalFetch = window.fetch.bind(window);
      window.fetch = async (url, config = {}) => {
        const method = (config.method || 'GET').toUpperCase();
        const requestKey = `${method} ${url}`;

        switch (true) {
          case requestKey.includes('/api/user/info'):
            return new Response(JSON.stringify(mockUtils.getUserInfo()));
          case requestKey.includes('/api/items'):
            const params = Object.fromEntries(new URLSearchParams(url.split('?')[1] || ''));
            return new Response(JSON.stringify(mockUtils.getItems(params)));
          case requestKey.includes('/api/boxes'):
            return new Response(JSON.stringify(mockUtils.getBoxes()));
          case requestKey.includes('/api/chat') && method === 'POST':
            const body = config.body ? JSON.parse(config.body) : {};
            return new Response(JSON.stringify(mockUtils.postChat(body.message)));
          default:
            return originalFetch(url, config);
        }
      };
    } catch (e) {
      console.warn('Mock拦截器初始化失败：', e);
    }
  }
};

// 3. 全部默认导出（Vue3风格，统一通过default暴露所有功能）
export default mockUtils;