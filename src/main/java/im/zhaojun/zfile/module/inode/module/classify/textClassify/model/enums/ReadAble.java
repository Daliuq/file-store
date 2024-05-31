package im.zhaojun.zfile.module.inode.module.classify.textClassify.model.enums;

public enum ReadAble {
    DOC("doc"),
    DOCX("docx"),
    TXT("txt"),
    MD("md");

    private final String extension;

    ReadAble(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
