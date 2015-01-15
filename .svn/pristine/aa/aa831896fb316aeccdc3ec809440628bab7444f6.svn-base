package com.HungryBells.service;

import com.HungryBells.dialog.FaceBook;
import com.HungryBells.dialog.GooglePlus;
import com.HungryBells.dialog.LinkedIn;
import com.HungryBells.dialog.Twitter;
import com.HungryBells.util.ServiceType;


public class ServiceProviderFactory {

    public static ServiceProvider getServiceProvider(ServiceType type) {
        
        switch (type) {
            case FACEBOOK:
                return new FaceBook();
            case TWITTER:
                return new Twitter();
            case GOOGLEPLUS:
                return new GooglePlus();
            case LINKEDIN:
                return new LinkedIn();
        }
        return null;
    }
}
