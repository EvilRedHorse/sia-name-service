package com.georgemcarlson.sianameservice.util.cacher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddressCache {
    private static final String[] DEV_FUND_ADDRESSES = new String[]{
        "4397ec4472eeab4665faf92a8fb935ed2d246f232177001ca6dc6533b2e14dfee38ca2072cdf",
        "e00ce3389d2c369abfe5fd4124e6cde9c4eda6d7d4a5d7d46b383427683b0f45dbb2f45011e9",
        "85e10f98730fbf00edcc9c2383bff23ffda1edd3742acb8230e8514a8e73611903851ae04410",
        "ba70fc13aee60597565a1e5f976bef0f101859debc77c0fdc50975f6ebb16102b8ef531b8818",
        "6eba61777a3868eead96638c19c8035284c7a39d65d8654894f6f45abdbde0660fe5a5cd48d9",
        "955998764da737ca3d400f3dccd53e62cc5488c35e4a4e46ce0bc77d9116402a94b71620072d",
        "0a647222460d15a18917b824a2ef7d6b3cbd30f57ba11b2cb5e92d0f66342ed5288610aa60f1",
        "b0b7eb0bb68b8a750bd1df4c97e3fa82ce1702c0cbbeef6c4c79551b87cb27ec44a0639d211a",
        "299c540bf4ecb5e4560b5eaa80cd08af7c8ec43d21a02ae23148261fdf64a17f1a0429d77936",
        "35f49b864094e92b1fedee8a8ae0ff93f52ac12c07f49d6dc030f69846124d67c495cef40a78",
        "a10697512b3e36299bf9052210b9dee0ff8e2de53058036326a253a2e3966f8b87b9d5c645bd",
        "bea48841787b7013ae8d839f836da193b57a9e7594b0c61c4fd66d2474e9786306c6a52e59a9",
        "db75c1e1847f87f169ecbf29c217fb0914e72dbafbec0d65a7e0c9aa3a1fe05e2375a5cf4ace",
        "f42e3fca0e3512dc9152293bc1e64c2de93dd4fd5529385e2775d52f55af2fc2a30e80dbc4c7",
        "8f4281dc43be5a6c98e6a055318ddd8e4aaeb19e046045c36c6c2b4f9523b0a2883c77de6c14",
        "3d141ebee3d0804520ff50a4b1756fc7069d2f016ad4c46aab5b8424ae29408e9881993d7f64",
        "f4f6a2262c210d9baed69a36825e1f1aa7f6413bf1a1a48e2087035d51dbc198cbe76d01f084",
        "95f6c5b67a291f4c0dc364ba84733c6602f34e894ddc5f86a852bc439f7252a0aa1a3b187907",
        "a45d328f9d4c1c1bc1c80bda2595e73adb62f467b923a3a041678ca284d8f810d516a869d127",
        "08f4d7a870e2455e5dd8b9edc909ff315d895043cca41a71f935dffcdc8939d1134d70026f1c",
        "42f53535b665ff00d99a7adfe713a30b3e3a46821430efb2f9aa795a240fc14674a2ef489ecf",
        "9a6fbc5d6024163b80fb02c8124d7cbcc265a63caa9bcb62a678c1f620dc4c085d70635367db",
        "37f1cd722a99e3179efb5f2b191bb8d21db70178ab25ccbe6d780bcfd4cb84ca1d5ef5a1a3ee",
        "2ef65a0a0c0ba90181b99ea6231414ed1199829a7b431b36409eaa2b4355d66c5e39755fa391",
        "fc3ad7fe2cbf23e109da6946201d933f2092de7da3199af833bd788aefb0379e972543858473",
        "59f14ed8cda25a4097d91f29c75634f5a6081027e4eaaf10804c003d9eb1ef3c2784b6fb9beb",
        "d3e204edb425625085840e6fde70f4634dfe86007b8169efbe79a4019894e0f0e67beab893f8",
        "7e4c378c9267ca366803d1fec6d933b3dc3765f1ce8ce6c89fe3cd5e99708f4b12214b0648af",
        "c511f02f10a4f56aeb91b9006453fac9f69b30643b9c418fecdc795286887e053e854d737394",
        "b88270e21fd885c5f6fcf69ccf1e5bbe089127349f6e5f10c4e42cc4debcbdc394e656d26f55",
        "5c985cf7036e2019b0d48b6b4c5065b8e95c8d6f68953b57554aaa89e0dfa707b264ed7938b0",
        "3cb1f752ae1921cefd16975fc65ddd8fadc787ca9236ca0caefa67b98cdfb131de3b9bbe30ed",
        "296701f51b1fc28246c606faaabff3ca5362aa3d849e08f09d58d41b02ec7e750ce3de237cba",
        "0cf6a25fa0e458cd44da2d3350d0e13a13eb5ce4e30fc23db6697b3d623faaf6a0fe0230da97",
        "d09ba49e6749953f0e67815ba420eed668f7ff7a7e94436da02f7b3ae0dc62b1623aa5bab5cb",
        "a88f497591fe2c67371fe32506075e9bfba46db273135bf0455b297002f97eb84031f62ed534",
        "525f62cbbbf7c01d467c83f342f1ec2213e6fd02fd663143d068d678cce275d34da5fb3bc459",
        "ea21af2b97593ebb66cfb3c360a0033f9346b0b841b0bbbe0c7eaa9448b47186fe433cba49b0",
        "8dce1f13ba2c83c53a8952b097e0c5e8cbdb1adb8228d21e74ec07cc559298bf66e5ae806885",
        "bb7787990be8ee196a8e195ecda5a36603fa295e44e72f2dd9bb4b0e36d5c43ceda7cc73f10c",
        "468bc0cd5e5e25b9547651dd41da6dbe4e20b2ebf1b8f9e646f1b1bd87836f295bfae7f86d41",
        "973fdda480f1728501298596499d59051e76f60013c49a2641536f07f17350902ab07a3ac1d8",
        "b00bb26d86c97a180ff8ef7bb7754a5f19fb46075c61ae5999859c13168384b9c11e0375b49d",
        "ee8c24a9ff4b46cb436d864f00e46920319e2f81f8df6974839a95fa5aacc619d74a9bed9df6",
        "bea92eee5c6ed31bba63b0f7fb97b64fade1754c113530df2de1703cdaf055f211acdd0bdd9e",
        "0fa8800a4eb99335e693ea9978d26aa0bb973b655baa1c67c0fb3fe8005eec624212b4d26abc",
        "2feb27587ab962871ec69f7bcd3b81e94c34ec280d26769ea8c743007abf7b860f51ef3a845e",
        "a2c50a0f4b7fadaf93cc0e592dd3df6a063fd4b72e462aa21373b45719379f2bf4ed999633c0",
        "a05fa09325eafe9e2271153ad6b80e35b3f8b73c7ac323cfaa461c807b2c24d362cf0fce2e6b",
        "8a0b5dd0776c4067bb34c80d15978dac16f364a52bc4a0aaaab533cbfb2bb552d88f8e8dea23"
    };

    public static List<String> getRandomAddresses(int amount, List<String> pool, Collection<String> excludingAddresses) {
        List<String> randomAddresses = new ArrayList<>();
        int i = 0;
        while (i < pool.size() - excludingAddresses.size() && i < amount) {
            int j = (int)(Math.random() * pool.size());
            if(!excludingAddresses.contains(pool.get(j))
                && !randomAddresses.contains(pool.get(j))
            ){
                randomAddresses.add(pool.get(j));
                i++;
            }
        }

        while (i < amount) {
            int j = (int)(Math.random() * DEV_FUND_ADDRESSES.length);
            if(!excludingAddresses.contains(DEV_FUND_ADDRESSES[j])
                && !randomAddresses.contains(DEV_FUND_ADDRESSES[j])
            ){
                randomAddresses.add(DEV_FUND_ADDRESSES[j]);
                i++;
            }
        }

        return randomAddresses;
    }

    public static String[] getDevFundAddresses() {
        return DEV_FUND_ADDRESSES;
    }

}
