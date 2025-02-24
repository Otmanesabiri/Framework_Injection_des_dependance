package ioc.example;

import ioc.framework.Component;

@Component("Dao")
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        return 10;
    }
}
