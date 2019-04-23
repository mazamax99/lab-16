import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

//метод с арг-ми: проверка заданности аргументов,первого файла,второго файла,размер буфера
//алгоритм: проверка исходного файла и проверка открытия(ошибка 1), проверка исходного файла и проверка записи(ошибка 2)
//,размер буфера-пока нет конца файла и пока буфер не пустой(проверка буфера на !=0 и 0<(ошибка 3)),функции nio, вывод прогресс копирования(остаток кол-во процентов)
// --srcfile=D:\test.txt --dstfile=D:\testlab16.txt --bufsize=4096
// --srcfile=D:\test1.txt --dstfile=D:\testlab16.txt --bufsize=4096
//System.out.print("Прогресс:" + fSize * proc + "%");
public class Main {
    public String FirstArgs[]= new String[3];
    public String injArgs[]= new String[3];
    static int count;
    static String src="" ;
    static String dst="";
    static int buff=0;
    public static void writeCopy() throws Exception {
        Path readFilepath = Paths.get(src);
        try (SeekableByteChannel fChan = Files.newByteChannel(readFilepath)) {
            long fSize = fChan.size();
            if(fSize==0)
                throw new Exception("Выбран файл с нулевым размером!");
            ByteBuffer mВuf = ByteBuffer.allocate(buff);
            if (buff > fSize) {
                mВuf = ByteBuffer.allocate((int) fSize);
                do {
                    count = fChan.read(mВuf);
                    if (count != -1) {
                        mВuf.rewind();
                        for (int i = 0; i < count; i++) {
                            System.out.println("Прогресс чтения:" + (float) ((float) i / (float) fSize * 100.0) + "%");
                        }
                        System.out.println("Прогресс чтения: 100%");
                    }
                }
                while (count != -1);

                FileChannel writeChan = (FileChannel) Files.newByteChannel(Paths.get(dst), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                {
                    ByteBuffer wВuf = ByteBuffer.allocate(buff);
                    wВuf = mВuf;
                    wВuf.rewind();
                    //Сделал Поцуков М.Н. Пэ-171
                    int counter = writeChan.write(wВuf);
                    for (int i = 0; i < counter; i++)
                        System.out.println("Прогресс записи:" + (float) ((float) i / (float) fSize * 100.0) + "%");
                }
                System.out.println("Прогресс записи: 100%");

            }
            else if(buff < fSize){
                float procsum=0.0f;
                float procsumw=0.0f;
                int cikli=((int)fSize/buff)+1;
                for(int k=1;k<=cikli;k++) {
                    float proc=0.0f;
                    float procw=0.0f;
                    do {
                        count = fChan.read(mВuf);
                        if (count != -1) {
                            mВuf.rewind();
                            for (int i = 1; i <= count; i++) {
                                proc=procsum+((float)i/(float)fSize);
                                System.out.println("Прогресс чтения:" + proc*100  + "%");
                            }
                            procsum=proc;
                        }
                    }
                    while (count != -1);

                    if(Files.exists(Paths.get(dst))){
                        FileChannel writeChan = (FileChannel) Files.newByteChannel(Paths.get(dst), StandardOpenOption.APPEND);
                        {
                            ByteBuffer wВuf = ByteBuffer.allocate(buff);
                            wВuf = mВuf;
                            wВuf.rewind();
                            int counter = writeChan.write(wВuf);
                            for (int i = 1; i <= counter; i++) {
                                procw = procsumw + ((float) i / (float) fSize);
                                System.out.println("Прогресс записи:" + procw * 100.0 + "%");
                                if (procw == 1)
                                    break;
                            }
                            procsumw = procw;

                        }
                    }
                    else {
                        FileChannel writeChan = (FileChannel) Files.newByteChannel(Paths.get(dst), StandardOpenOption.APPEND,StandardOpenOption.CREATE);
                        {
                            ByteBuffer wВuf = ByteBuffer.allocate(buff);
                            wВuf = mВuf;
                            wВuf.rewind();
                            int counter = writeChan.write(wВuf);
                            for (int i = 1; i <= counter; i++) {
                                procw = procsumw + ((float) i / (float) fSize);
                                System.out.println("Прогресс записи:" + procw * 100.0 + "%");
                                if (procw == 1)
                                    break;
                            }
                            procsumw = procw;

                        }
                    }
                }
            }
        }
        catch(IOException e){
            System.out.println("Oшибкa ввода-вывода" + e);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("[Enter ur paths and BuffSize]-for example:\r\n--srcfile=anySrcPath --dstfile=anyDstPath --bufsize=4096");
        String FirstArgs[]= new String[3];
        String injArgs[]= new String[3];
        try {
            Scanner scan=new Scanner(System.in);
            String stroka=scan.nextLine();
            String[] arg=stroka.split(" ");
            for(int i=0;i<arg.length;i++){
                String[] arguments= arg[i].split("=");
                FirstArgs[i]=arguments[0];
                injArgs[i]=arguments[1];
                //System.out.println(FirstArgs[i]);
                //System.out.println(injArgs[i]);
            }
            for(int i=0;i<FirstArgs.length;i++) {
                if (FirstArgs[i].equals("--srcfile")){
                    src = injArgs[i];}
                else if (FirstArgs[i].equals("--dstfile")){
                    dst = injArgs[i];}
                else if (FirstArgs[i].equals("--bufsize")){
                    buff = Integer.parseInt(injArgs[i]);}
                else{
                    throw new Exception ("Не найден параметр!");
                }
            }
        }
        catch(Exception e){
            throw new Exception("Введены неправильно параметры! Введите как показано в примере!");
        }

        writeCopy();

    }
}
