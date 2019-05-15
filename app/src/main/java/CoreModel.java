import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class CoreModel extends ViewModel {
    public MutableLiveData<String> code = new MutableLiveData<>();
    public MutableLiveData<String> lang = new MutableLiveData<>();

    public CoreModel(String code,String lang){
        this.code.setValue(code);
        this.lang.setValue(lang);
    }
}
