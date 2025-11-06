// 1. 根据数据库结构更新Mock数据
const MOCK_DATA = {
	// 用户信息
	USER_INFO: {
		code: 200,
		data: {
			id: 1,
			user_account: "user_xiaoming",
			username: "小明",
			phone: "13800138000",
			create_time: "2024-05-01T10:30:00.000Z",
			update_time: "2025-10-23T20:56:35.000Z"
		}
	},

	// 物品列表 - 根据t_item表结构
	ITEMS: [{
			id: 1,
			item_code: "ITEM-20240501-1420",
			box_id: 1,
			auto_recognize_name: "笔记本",
			manual_edit_name: "工作笔记本",
			item_tag: "办公用品",
			item_desc: "2024年公司年会发放，封面有logo",
			put_in_time: "2024-05-01T14:20:00.000Z",
			expire_time: null,
			is_valid: 1,
			create_time: "2025-10-23T20:56:35.000Z",
			update_time: "2025-10-23T20:56:35.000Z",
		},
		{
			id: 2,
			item_code: "ITEM-20240502-1530",
			box_id: 2,
			auto_recognize_name: "钥匙",
			manual_edit_name: null,
			item_tag: "日常用品",
			item_desc: "家门钥匙，带银色挂饰",
			put_in_time: "2024-05-02T15:30:00.000Z",
			expire_time: null,
			is_valid: 1,
			create_time: "2025-10-23T20:56:35.000Z",
			update_time: "2025-10-23T20:56:35.000Z",
		}
	],

	// 收纳盒列表 - 根据t_box表结构
	BOXES: {
		code: 200,
		data: [{
				id: 1,
				box_code: "ESP32-CAM-20240501-001",
				user_id: 1,
				box_name: "书房主收纳盒",
				box_type: 0,
				idx_user_box_type: 1,
				status: 1,
				rssi: null,
				battery: 85,
				last_heartbeat_time: "2024-05-01T11:20:30.000Z",
				create_time: "2024-05-01T10:35:00.000Z",
				update_time: "2025-10-23T20:56:35.000Z",
			},
			{
				id: 2,
				box_code: "ESP32-CAM-20240502-001",
				user_id: 2,
				box_name: "卧室子收纳盒",
				box_type: 0,
				idx_user_box_type: 0,
				status: 1,
				rssi: -55,
				battery: 70,
				last_heartbeat_time: "2024-05-02T10:10:20.000Z",
				create_time: "2024-05-02T09:20:00.000Z",
				update_time: "2025-10-23T20:56:35.000Z",
			}
		]
	},

	// 聊天推荐物品
	CHAT_RECOMMEND_ITEMS: [{
			id: 1,
			manual_edit_name: "工作笔记本",
			auto_recognize_name: "笔记本",
			box_id: 1,
			item_tag: "办公用品"
		},
		{
			id: 2,
			manual_edit_name: null,
			auto_recognize_name: "钥匙",
			box_id: 2,
			item_tag: "日常用品"
		}
	]
};

// 2. Mock工具函数
const mockUtils = {
	// 获取用户信息
	getUserInfo: () => MOCK_DATA.USER_INFO,

	// 获取物品列表（支持分页）
	getItems: (params = {}) => {
		const page = Number(params.page || 1);
		const pageSize = 10;
		const start = (page - 1) * pageSize;
		const pageData = MOCK_DATA.ITEMS.slice(start, start + pageSize);
		return {
			code: 200,
			data: pageData,
			total: MOCK_DATA.ITEMS.length
		};
	},

	// 获取收纳盒列表
	getBoxes: () => MOCK_DATA.BOXES,

	// 聊天回复
	postChat: (message = "") => ({
		code: 200,
		data: {
			reply: `已收到：「${String(message).slice(0, 20)}」，为您推荐相关物品：工作笔记本、钥匙`,
			items: MOCK_DATA.CHAT_RECOMMEND_ITEMS
		}
	}),

};

// 3. 默认导出
export default mockUtils;