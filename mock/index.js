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

export default {
    getCategoryIcon
}
