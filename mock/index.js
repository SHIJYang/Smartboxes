// 将原先依赖 mockjs 的实现替换为轻量安全的导出，避免在未安装 mockjs 时导致项目启动失败.

// 导出图标映射（与原逻辑保持一致）
export const getCategoryIcon = (category) => {
    const icons = {
        drawer: '🗄️',
        shelf: '📚',
        wardrobe: '👕',
        box: '📦',
        default: '📁'
    }
    return icons[category] || icons.default
}

// 如果有需要在导入时触发的开发环境侧效果，可以在这里添加（目前保持无副作用以保证安全）
// export const initMock = () => { /* 可选：在开发时执行的初始化逻辑 */ }

export default {
    getCategoryIcon
}
