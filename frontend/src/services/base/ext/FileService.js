import BaseService from "../BaseService";

class FileService extends BaseService {
    constructor() {
        super("/files");
    }

    checkHashExists(hash, userId) {
        return this.get("/check", {
            params: { hash, userId }
        });
    }

    uploadFile(file, frontendHash, ownerId) {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("ownerId", ownerId); // узгоджено з бекендом
        formData.append("frontendHash", frontendHash); // узгоджено з бекендом

        return this.post("/upload", formData, {
            headers: { "Content-Type": "multipart/form-data" }
        });
    }

    getAllFiles(userId, page = 0, size = 10) {
        return this.get("", {
            params: { userId, page, size }
        });
    }

    deleteFile(id) {
        return this.delete(`/${id}`);
    }
}

export default new FileService();
