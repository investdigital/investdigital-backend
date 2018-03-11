package info.investdigital;

import info.investdigital.dao.FundCommentRepo;
import info.investdigital.dao.FundOfTagRepo;
import info.investdigital.dao.FundRepo;
import info.investdigital.dao.FundTagRepo;
import info.investdigital.entity.FundComment;
import info.investdigital.entity.FundOfTag;
import info.investdigital.entity.FundTag;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvestdigitalApplicationTests {

	@Test
	public void contextLoads() {
		get("http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=zzf,20&gs=0&sc=zzf&st=desc&sd=2016-12-13&ed=2017-12-13&qdii=&tabSubtype=,,,,,&pi=1&pn=50&dx=1&v=0.9199814353030342");
	}

	@Resource
    FundRepo fundRepo;
	@Resource
    FundTagRepo fundTagRepo;
	@Resource
    FundOfTagRepo fundOfTagRepo;
	@Resource
	FundCommentRepo fundCommentRepo;
	@Test
	public void createTags(){
		Iterable<Fund> funds = fundRepo.findAll();
		Iterable<FundTag> fundTags = fundTagRepo.findAll();

		List<FundOfTag> list = new ArrayList<>();
		for(Fund fund:funds){
			int n = fund.getId().intValue() % 4;
			int i = 0;
			for(FundTag fundTag : fundTags){
				if(i>n){
					break;
				}
				FundOfTag fundOfTag = new FundOfTag();
				fundOfTag.setFundId(fund.getId());
				fundOfTag.setTagId(fundTag.getId());

				list.add(fundOfTag);
				i++ ;
			}

		}
		fundOfTagRepo.save(list);
	}
	@Test
	public void createComment(){
		Integer[] users = {243461,252858,294761,297316,298959,299685,300044,306201,306542,307144,307653,307775,309061,311121,311378,312047,312333,314778,316089,316288,317526,317943,320723,322097,322107,322131,322132,322139,322141,322492,322732,323437,324595,325190,325288,325649,331066,331067,331068};
		Integer[] funds = {28,25,29,34,27,35,36,31,18,23,21,22,17,32,19,33,20,26,30,24};
		String[] comments = {"乱世之中，唯有强者才能掌控自己的命运。","王者独尊嬴政说：天上天下，唯朕独尊!","根源之目杨戬说：尽情驰骋吧，纵使天地也太狭小!与其受制于人，不妨听命自己。","无双之魔吕布说：从此刻开始，战场由我一人主宰!可有人敢与我一战!","苍天翔龙赵云说：勇者之誓，甚于生死!心怀不惧，方能翱翔于天际!","桀骜炎枪哪咤说：不能击败我的，会让我更强大!","剑圣宫本武藏说：天下无双!告诉你个秘密：我，是无敌的!","禁血狂兽张飞说：修身，养性。心有猛虎。百万军中，取人首级如探囊取物!","鲜血枭雄曹操说：宁教我负天下人!力量也会臣服于我!","狂战士典韦说：身体里沉睡的野兽，觉醒了!","乱世虽乱，却并非没有秩序，光明与正义始终存在于人们心底。","最终兵器白起说：身在黑暗，心向光明!","仁德义枪刘备说：除暴安良是责任，行善积德是兴趣!出来混，最重要的是讲义气!","圣域余晖雅典娜说：正视你的邪恶!畏惧信仰!畏惧力量!","圣骑之力亚瑟说：永不背叛!王者背负，王者审判，王者不可阻挡!","虚灵城判钟馗说：维持秩序!吾之内涵，有容乃大!吾之身躯，无欲则刚!","燃魂重炮黄忠说：正义或许会迟到，但绝不会忘记砸到你头顶!","面对罪恶邪恶与不公平，断案大师狄仁杰说：打击罪恶!为无辜者代言!","王都密探李元芳说：做坏蛋，要有结局会杯具的觉悟!","时势造英雄，不甘埋没于时代浪潮的热血男儿们在无尽的战斗中发出了自己最嘹亮的声音。","力拔山兮气盖世的霸王项羽说：命运?不配做我的对手!我命由我!对自己的男儿自信，无比自信!","热烈之斧程咬金说：进攻是最好的防守!就算失败，也要摆出豪迈的姿态!","苍狼末裔成吉思汗说：雄鹰不为暴风折翼，狼群不因长夜畏惧!","一骑当千关羽说：决定了内心的正道，便绝无动摇!生命与信念，都交托阁下!","齐天大圣孙悟空说：取经之路就在脚下!超出三界之外，不在五行之中!","精英酋长牛魔说：突进的野兽之道!牛气冲天，纯爷们!","破灭刃峰铠说：以绝望挥剑!","不羁之风夏侯惇说：独眼是男人的浪漫!没错!俺就是呼唤胜利的男神!","机关造物鲁班七号说：鲁班大师，智商250，膜拜。记得膜拜!","暗影刀锋兰陵王说：刀锋所划之地，便是疆土。","叛逆吟游高渐离说：该我上场表演了!","双面君主刘邦说：有时候想活命，就得以毒攻毒。不客观地说，我是个好人!","正义爆轰廉颇说：伤痕，是男子汉的勋章。","国士无双韩信说：到达胜利之前，无法回头。","青莲剑仙李白说：一篇诗，一斗酒，一曲长歌，一剑天涯。但愿长醉不复醒!","在这片广阔的大陆上，奋斗不息的不仅有顶天立地的男子汉，还有不让须眉的巾帼英雄。","传说之刃花木兰说：谁说女子不如男?永不放弃!不会认输!","魅惑之狐妲己说：请尽情吩咐妲己吧，主人。","恋之微风小乔说：花会枯萎，爱永不凋零!恋爱和战斗，都要勇往直前!","野蛮之锤钟无艳说：女人心，海底针!给你的麻烦开个价吧!","天籁弦音蔡文姬说：飞舞战场的美少女，大活跃Q!","女帝武则天说：奉我为主!","千金重弩孙尚香说：大小姐驾到!统统闪开!","信念之刃阿轲说：只相信本能。","冰雪之华王昭君说：白梅落下之日，归去故里之时!凛冬已至。","洛神降临甄姬说：若轻云之蔽月，若流风之回雪!","月光之女露娜说：燃烧的剑，燃烧的心!剑是用来挑战强者的!","永恒之月芈月说：拥有了青春，也就拥抱了永恒!","森之风灵虞姬说：明媚如风，轻盈似箭!","绝世舞姬貂蝉说：华丽又漂亮地生存到最后!","生于这大千乱世之中，或无奈，或振奋，不同的英雄选择了不同的道路，看破红尘的智者们也总结出了自己的认识。","万古长明老夫子说：有朋自远方来，不亦乐乎。","暗夜萝莉安琪拉说：知识，就是力量!","拳僧达摩说：邪不胜正!道路很远，脚步更长。肩挑凡世，拳握初心。","噬灭日蚀东皇太一说：万物皆可知!展现进化的可能性!","万物有灵鬼谷子说：万物皆有灵!理解世界，而非享受它。","逆流之时孙膑说：时间和波浪，变化无常!","铁血都督周瑜说：掌控全局!用头脑，而不是武力!","善恶怪医扁鹊说：操纵生死?哼，愚不可及!生存还是死亡，这是个问题。","太古魔导姜子牙说：愿者上钩!这是多么痛彻的领悟。","绝代智谋诸葛亮说：天下如棋，一步三算!运筹帷幄之中，决胜千里之外!","言灵之书张良说：我思故我在!","炼金大师太乙真人说：做人呢，最重要的是开心!","逍遥梦幻庄周说：蝴蝶是我，我就是蝴蝶。美妙的长眠，值得高歌一曲。","和平守望墨子说：生存，就是最精彩的战斗。","不得不说，王者大陆真的很奇妙，热血的战斗从未停止，爱与希望也贯穿始终。","半神之弓后羿说：赞美太阳!","沧海之曜大乔说：点亮的心，不会轻易熄灭!破碎的奇迹，好过没有。苦恼的希望，胜于迷惘。","暴走机关刘禅说：蓉城是我家，老爹最伟大!","淬命双剑干将莫邪说：生死契阔，与子成说。执子之手，与子偕老!","远游之枪马可波罗说：世界不止眼前的苟且，还有诗和远方!世界那么大，我想来看看。"};
		int ulen = users.length;
		int flen = funds.length;
		int clen = comments.length;

		List<FundComment> list = new ArrayList<>();
		for(Integer id : funds){
			Random randomUser = new Random();
			Random randomCom = new Random();
			for( int i =0; i< (ulen+flen+clen)%ulen;i++) {
				int u = randomUser.nextInt(ulen);
				int c = randomCom.nextInt(clen);

				if(u == ulen){
					u = u - 1;
				}
				if(c == clen){
					c = c - 1;
				}

				FundComment fundComment = new FundComment();
				fundComment.setFundId(Long.valueOf(id));
				fundComment.setUserId(Long.valueOf(users[u]));
				fundComment.setComments(comments[c]);

				list.add(fundComment);
				System.out.println(u + "--" + c);
			}
		}

		fundCommentRepo.save(list);

		//System.out.println(ulen +", "+flen+", "+clen);
	}
	/**
	 * 发送 get请求
	 */
	private void get(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				System.out.println("--------------------------------------");
				// 打印响应状态
				System.out.println(response.getStatusLine());
				if (entity != null) {
					// 打印响应内容长度
					System.out.println("Response content length: " + entity.getContentLength());
					// 打印响应内容
					System.out.println("Response content: " + EntityUtils.toString(entity));
				}
				System.out.println("------------------------------------");
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void time(){
		System.out.println("*********************************** "+System.currentTimeMillis());
		System.out.println("----------------------------");
	}
}
