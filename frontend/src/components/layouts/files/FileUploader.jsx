import React, {useState} from "react";
import CryptoJS from "crypto-js";
import FileService from "../../../services/base/ext/FileService";

const FileUploader = () => {
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState("");
  const userId = 1;

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const arrayBufferToWordArray = (ab) => {
    const i8a = new Uint8Array(ab);
    const wa = [];
    for (let i = 0; i < i8a.length; i += 4) {
      wa.push(
          (i8a[i] << 24) |
          (i8a[i + 1] << 16) |
          (i8a[i + 2] << 8) |
          (i8a[i + 3])
      );
    }
    return CryptoJS.lib.WordArray.create(wa, i8a.length);
  };

  const computeHash = async (file) => {
    const arrayBuffer = await file.arrayBuffer();
    const hashBuffer = await crypto.subtle.digest("SHA-256", arrayBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, "0")).join("");
  };

  const uploadFile = async () => {
    if (!file) return;

    setStatus("Обчислення хешу...");
    const hash = await computeHash(file);

    setStatus("Перевірка, чи файл уже існує...");
    try {
      const resCheck = await FileService.checkHashExists(hash, userId);
      console.log(resCheck)
      if (resCheck.exists) {
        setStatus("Файл вже існує");
        return;
      }
    } catch (error) {
      console.log(error)
      setStatus("Помилка перевірки файлу");
      return;
    }

    setStatus("Завантаження файлу...");
    try {
      await FileService.uploadFile(file, hash, userId);
      setStatus("Файл успішно завантажено!");
    } catch (err) {
      setStatus("Помилка при завантаженні");
    }
  };

  return (
      <div>
        <input type="file" onChange={handleFileChange} />
        <button onClick={uploadFile}>Завантажити</button>
        <p>{status}</p>
      </div>
  );
};

export default FileUploader;
