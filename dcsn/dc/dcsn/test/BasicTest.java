package dc.dcsn.test;

import os.bson.BSON;
import os.bson.binary.MD5;
import os.mongo.Query;

public class BasicTest {
	public static void main(String[] args){
		byte[] p1 = BSON.encode(
			Query.start("id").is(
				Query.start("p").is("GP").
				and("i").is("107523059667697353868")
			)
		);
		byte[] p2 = BSON.encode(
			Query.start("id").is("GP@107523059667697353868")
		);
		byte[] p3 = BSON.encode(
			Query.start("id").is(new MD5("GP"))
		);
		print(p1.length+" "+p2.length+" "+p3.length+" "+(p1.length>p2.length));
	}

	private static void print(Object object) {
		System.out.println(object);
	}
}
