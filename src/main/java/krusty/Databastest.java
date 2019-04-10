package krusty;

public class Databastest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String cookie = "Nut Cookie";
		String a = "update Warehouse, Ingridients set quantity = quantity - (amount*54)"
				+ " where Warehouse.ingridient = Ingridients.ingridient and Ingridients.pName = " + cookie +"; ";
		int id = 3;
System.out.println("{ \"status\": \"ok\" \n" + "  \"id\": " + id +  " }\n") ;
		
		//Database db = new Database();
		//db.connect();
		
		//System.out.println(db.getRecipes(null, null));
		
		
		
		
		
		
	}

}
