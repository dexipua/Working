import BaseService from "../BaseService";

class ChatService extends BaseService {
    constructor() {
        super("/chats"); // базовий шлях для чатів
    }

    // Отримати всі чати користувача (може бути пагінація, якщо потрібно)
    getMyChats() {
        return this.get("/my");
    }

    // Створити приватний чат між двома користувачами
    createPrivateChat(userId1, userId2) {
        return this.post("/private", {userId1, userId2});
    }

    // Створити груповий чат із назвою, учасниками та лімітом
    createGroupChat(title, userIds, maxParticipants = 10) {
        return this.post("/group", {title, userIds, maxParticipants});
    }

    // Отримати чат за id
    getChat(id) {
        return this.get(`/${id}`);
    }
}

export default new ChatService()

// Можна додати інші методи: оновлення,
