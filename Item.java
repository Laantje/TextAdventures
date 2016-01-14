public abstract class Item {
	private String description;
    private int weigth;

    public Item() {
        this.setWeigth(0);
    }
    
    public String getDescription() {
        return description;
    }
    
    public void use(Object o) {
		System.out.println("using Item on an Object");
	}

	public int getWeigth() {
		return weigth;
	}

	public void setWeigth(int weigth) {
		this.weigth = weigth;
	}
}
