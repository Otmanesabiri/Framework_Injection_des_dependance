package ioc.example;

import ioc.framework.Component;
import ioc.framework.Inject;

@Component("Metier")
public class MetierImpl implements IMetier {
    @Inject("Dao")
    private IDao dao;

    @Override
    public double calcul() {
        return dao.getData() * 2;
    }
}
