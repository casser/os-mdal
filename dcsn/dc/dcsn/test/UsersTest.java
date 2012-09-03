package dc.dcsn.test;

import os.json.JSON;
import os.mdal.core.DAL;
import os.utils.Types;
import dc.dcsn.models.Platform;
import dc.dcsn.models.pl.Account;
import dc.dcsn.models.pl.Announcement;
import dc.dcsn.models.pl.Application;
import dc.dcsn.models.pt.Game;
import dc.dcsn.models.pt.Install;
import dc.dcsn.models.pt.Message;
import dc.dcsn.models.pt.User;


public class UsersTest {
	public static void main(String[] args) throws Exception{
		
		DAL.Enhancer.enhance("dc.dcsn.models.pl.Announcement");
		DAL.Enhancer.enhance("dc.dcsn.models.pl.Account");
		DAL.Enhancer.enhance("dc.dcsn.models.pl.Application");
		DAL.Enhancer.enhance("dc.dcsn.models.pt.Install");
		DAL.Enhancer.enhance("dc.dcsn.models.pt.Message");
		DAL.Enhancer.enhance("dc.dcsn.models.pt.Game");
		DAL.Enhancer.enhance("dc.dcsn.models.pt.User");
		/*
		System.out.println(BSON.encode(Query.start("i").is("reborni").getQuery()).length);
		System.out.println(BSON.encode(Query.start("i").is(BsonId.get()).getQuery()).length);
		
		
		System.out.println(new BsonId(User.class,"reborn-empire"));
		
		JSON.print(User.get(new User.Id("U1")));
		
		User p = new User();
		p.setId(new User.Id("U1"));
		p.setUsers(new User.Accounts(
			new Account.Id("FB@U1"),new Account.Id("DC@U2")
		));
		p.setGames(new User.Games(new Game.Id("G1"),new Game.Id("G2")));
		p.save();
		JSON.print(p);
		*/
		Types.register(Announcement.class);
		Types.register(Account.class);
		Types.register(Application.class);
		Types.register(Install.class);
		Types.register(Message.class);
		Types.register(Game.class);
		Types.register(User.class);
		
		JSON.print(Types.getTypesMap());
		
		Application app = Application.get(new Application.Id("FB@279889278735585"));
		if(app==null){
			app = new Application(new Application.Id("FB@279889278735585"));
			app.save();
		}
		Account acc = Account.get(new Account.Id("FB@707387216"));
		if(acc==null){
			acc = new Account(new Account.Id("FB@707387216"));
			acc.save();
		}
		
		init("FB","279889278735585","707387216");
		DAL.shutdown();
	}
	
	public static void init(String platformId, String appId, String userId){
		Platform platform 	= Platform.valueOf(platformId);
		Application app 	= platform.id(Application.Id.class,appId).model();
		Account 	acc 	= platform.id(Account.Id.class,userId).model();
		JSON.print(app.game());
		JSON.print(acc.user());
	}
	
	public static void print(Object obj){
		System.out.println(obj);
	}
}
