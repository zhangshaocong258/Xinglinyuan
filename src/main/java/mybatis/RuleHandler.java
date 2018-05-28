package mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsc on 2017/11/16.
 */
public class RuleHandler {
    public static void main(String args[]) {
//        selectById();
//        selectByName("x");
//        selectAll();
//        insert();
        insertAll();
//        update();
//        remove();

    }
    public static void selectById(int id) {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            RuleDao ruleDaoMapper = sqlSession.getMapper(RuleDao.class);
            Rule rule = ruleDaoMapper.selectRuleById(id);
            System.out.println(rule.getZhumai());
        } finally {
            sqlSession.close();
        }
    }

    public static Rule selectByName(String name) {
        SqlSession sqlSession = getSessionFactory().openSession();
        Rule rule;
        try {
            RuleDao ruleDaoMapper = sqlSession.getMapper(RuleDao.class);
            rule = ruleDaoMapper.selectRuleByName(name);
        } finally {
            sqlSession.close();
        }
        return rule;
    }

    public static void selectAll() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            RuleDao ruleMapper = sqlSession.getMapper(RuleDao.class);
            List<Rule> rule = ruleMapper.selectAllRules();
            System.out.println(rule.get(0).getId());
        } finally {
            sqlSession.close();
        }
    }

    public static void insert() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            RuleDao ruleMapper = sqlSession.getMapper(RuleDao.class);
            Rule rule = new Rule(1, "aa","zsc", "11", "faff", "11", "faff", "aa", "aa","aa");//xml中设置是否自动编号
            ruleMapper.insertRule(rule);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    public static void insertAll() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            RuleDao RuleMapper = sqlSession.getMapper(RuleDao.class);
            //                        心气虚,     主症,   次证,    主舌质,    次舌质,    主舌苔,    次舌苔,    主脉,   次脉
            Rule rule1 = new Rule(1, "x","!aaw,aba,adr,acv,aar","aau,adq,add,adl,ado,ads,adp,aay,aaz",
                    "淡白", "淡红,胖,嫩,淡白",
                    "白,润", "薄,白,腻",
                    "虚","细,弱,涩,沉,缓,结,代");//心气虚
            Rule rule2 = new Rule(2, "g","!aaz,!aau,!aax,@abn,@acj,@abm,@ack,@acl,@acd,@aci,#adj,#aao,#aas,#ade,#ads",
                    "adc,aar,aas,aak,aal,aam,adg,adf,adk,aau,aav,aaf,aab,aad,aae,aax,adp,aao,aan,aaz,abm,adr,abp,act,acs,acw,acx,acy,acl,aco,acp,aau,add,acg,ach,abq,abz,acb,aby,aca,abw",
                    "红", "红,淡红,裂,绛",
                    "少", "少津,少,薄,白,黄,无",
                    "弦,细,数","数,沉,细,弦");//肝肾
            Rule rule3 = new Rule(3, "p","!aap,!abl,abp,abx,acv",
                    "aar,aat,adn,adm,adq,add,abp,abd,abq,abx,adb,aag,abx,aaj,abc,abv,adc,acv,acz,aaq,abz,aby,aca,adk,adf,ace,aci,acg,aci",
                    "淡白,胖,嫩,齿", "紫,胖,嫩",
                    "白", "白,腻,润",
                    "缓,弱","虚,沉,细,弱,数");//脾胃
            List<Rule> list = new ArrayList<Rule>();
            list.add(rule1);
            list.add(rule2);
            list.add(rule3);
            RuleMapper.insertAllRule(list);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }


    public static void update() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            RuleDao ruleMapper = sqlSession.getMapper(RuleDao.class);
            Rule rule = ruleMapper.selectRuleById(7);
            ruleMapper.updateRule(rule);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    public static void remove() {
        SqlSession sqlSession = getSessionFactory().openSession();
        try {
            RuleDao ruleMapper = sqlSession.getMapper(RuleDao.class);
            ruleMapper.deleteRule(1);
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
