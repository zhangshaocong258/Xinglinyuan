package mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import preprocess.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsc on 2017/11/16.
 */
public class DiscaseHandler {

    private static final String caseDataPath = "src\\main\\resources\\合并\\处理后";
    private static List<File> fileList = new ArrayList<File>();
    public static void main(String args[]) {
//        selectById();
//        selectAll();
//        insert();
        insertAll();
//        update();
//        remove();

    }
    public static void selectById(int id) {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            DiscaseDao DiscaseDaoMapper = sqlSession.getMapper(DiscaseDao.class);
            Discase discase = DiscaseDaoMapper.selectDiscaseById(id);
            System.out.println(discase.getMedicalHis());
        } finally {
            sqlSession.close();
        }
    }


    public static void selectAll() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            DiscaseDao discaseMapper = sqlSession.getMapper(DiscaseDao.class);
            List<Discase> Discase = discaseMapper.selectAllDiscases();
            System.out.println(Discase.get(0).getId());
        } finally {
            sqlSession.close();
        }
    }

    public static void insert() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            DiscaseDao DiscaseMapper = sqlSession.getMapper(DiscaseDao.class);
            Discase Discase = new Discase(1, "aa","zsc", "11", "faff");//xml中设置是否自动编号
            DiscaseMapper.insertDiscase(Discase);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    public static void insertAll() {
        SqlSession sqlSession = getSessionFactory().openSession();

        try {
            DiscaseDao discaseMapper = sqlSession.getMapper(DiscaseDao.class);
            List<Discase> list = new ArrayList<Discase>();
            genFeature(caseDataPath);
            for (File file : fileList) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                Discase discase = new Discase(1, br.readLine(), br.readLine(), br.readLine(),br.readLine());
                list.add(discase);
            }
            discaseMapper.insertAllDiscase(list);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    public static void genFeature(String path) throws Exception {
        File folder = new File(path);
        if (folder.isFile()) {
            fileList.add(folder);
        } else if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                genFeature(file.getAbsolutePath());
            }
        }
    }


    public static void update() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            DiscaseDao discaseMapper = sqlSession.getMapper(DiscaseDao.class);
            Discase Discase = discaseMapper.selectDiscaseById(7);
            discaseMapper.updateDiscase(Discase);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    public static void remove() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            DiscaseDao discaseMapper = sqlSession.getMapper(DiscaseDao.class);
            discaseMapper.deleteDiscase(1);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    //Mybatis 通过SqlSessionFactory获取SqlSession, 然后才能通过SqlSession与数据库进行交互
    private static SqlSessionFactory getSessionFactory() {
        SqlSessionFactory sessionFactory = null;
        String resource = "mybatis\\configuration.xml";
        try {
            sessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }
}
