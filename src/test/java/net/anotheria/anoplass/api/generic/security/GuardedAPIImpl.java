package net.anotheria.anoplass.api.generic.security;

import net.anotheria.anoplass.api.AbstractAPIImpl;

public class GuardedAPIImpl extends AbstractAPIImpl implements GuardedAPI{

	private int counter = 0;
	

	@Override
	public void doSomeAction() {
		System.out.println("DONE SOME ACTION");
		counter++;
	}

	@Override
	public boolean mayIDoSomeAction() {
		return true;
	}
	
	public int actionCounter(){
		return counter;
	}
	
}
