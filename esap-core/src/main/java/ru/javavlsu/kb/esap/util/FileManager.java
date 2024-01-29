package ru.javavlsu.kb.esap.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//TODO: добавить синхронизацию файловой системы с бд
// (Например, если файл был удален непосредственно из файловой системы, а не с помощью метода)
// Сделать генерацию документов для врачей
// То есть чтобы загрузить можно было шаблон заключения, а программа сама генерировала его и отдавала пользователю.
@Component
public class FileManager {

    private final String DIRECTORY_PATH = "esap-core\\src\\main\\resources\\storage";

    public void upload(byte[] resource, String keyName) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH, keyName);
        Files.write(path, resource);
    }

    public Resource download(String key) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH, key);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException();
        }
    }

    public void delete(String key) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH + key);
        Files.delete(path);
    }
}
