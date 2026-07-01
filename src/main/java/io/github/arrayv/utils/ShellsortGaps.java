package io.github.arrayv.utils;

public enum ShellsortGaps {
    // Notable gap sequences
    ORIGSHELL {
        @Override
        public int[] getGaps(int n) {
            java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
            for (int k = n; k >= 1; k = k / 2) {
                list.add(k);
            }
            int[] result = new int[list.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = list.get(i);
            }
            return result;
        }
        @Override
        public String getName() { return "Shell's Original"; }
    },
    FRANKLAZ {
        @Override
        public int[] getGaps(int n) {
            java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
            for (int k = n; k >= 1; k = k < 2 ? 0 : (k / 2) | 1) {
                list.add(k);
            }
            int[] result = new int[list.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = list.get(i);
            }
            return result;
        }
        @Override
        public String getName() { return "Frank and Lazarus"; }
    },
    HIBBARD {
        private final int[] gaps = {536870911, 268435455, 134217727, 67108863, 33554431, 16777215, 8388607, 4194303, 2097151, 1048575, 524287, 262143, 131071, 65535, 32767, 16383, 8191, 4095, 2047, 1023, 511, 255, 127, 63, 31, 15, 7, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "2^k - 1"; }
    },
    E_HIBBRD {
        private final int[] gaps = {536870913, 268435457, 134217729, 67108865, 33554433, 16777217, 8388609, 4194305, 2097153, 1048577, 524289, 262145, 131073, 65537, 32769, 16385, 8193, 4097, 2049, 1025, 513, 257, 129, 65, 33, 17, 9, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "2^k + 1"; }
    },
    KNUTH_P3 {
        private final int[] gaps = {581130733, 193710244, 64570081, 21523360, 7174453, 2391484, 797161, 265720, 88573, 29524, 9841, 3280, 1093, 364, 121, 40, 13, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Knuth's Powers of 3"; }
    },
    INCERPI {
        private final int[] gaps = {852913488, 343669872, 114556624, 49095696, 21479367, 8382192, 3402672, 1391376, 463792, 198768, 86961, 33936, 13776, 4592, 1968, 861, 336, 112, 48, 21, 7, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Incerpi and Sedgewick"; }
    },
    SEDGE_82 {
        private final int[] gaps = {268460033, 67121153, 16783361, 4197377, 1050113, 262913, 65921, 16577, 4193, 1073, 281, 77, 23, 8, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Sedgewick's Powers of 4"; }
    },
    SEDGE_86 {
        private final int[] gaps = {603906049, 268386305, 150958081, 67084289, 37730305, 16764929, 9427969, 4188161, 2354689, 1045505, 587521, 260609, 146305, 64769, 36289, 16001, 8929, 3905, 2161, 929, 505, 209, 109, 41, 19, 8, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Sedgewick's Odd-Even"; }
    },
    GBY {
        @Override
        public int[] getGaps(int n) {
            java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
            for (int k = n; k >= 1; k = k < 2 ? 0 : (k < 5 ? 1 : (5 * k - 1) / 11)) {
                list.add(k);
            }
            int[] result = new int[list.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = list.get(i);
            }
            return result;
        }
        @Override
        public String getName() { return "Gonnet and Baeza-Yates"; }
    },
    TOKUDA {
        private final int[] gaps = {510097200, 226709866, 100759940, 44782196, 19903198, 8845866, 3931496, 1747331, 776591, 345152, 153401, 68178, 30301, 13467, 5985, 2660, 1182, 525, 233, 103, 46, 20, 9, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Tokuda"; }
    },
    CURA_25O {
        private final int[] gaps = {680296988, 302354217, 134379652, 59724290, 26544129, 11797391, 5243285, 2330349, 1035711, 460316, 204585, 90927, 40412, 17961, 7983, 3548, 1577, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Ciura(701) F2.25"; }
    },

    // Other gap sequences
    POW_OF_2 {
        private final int[] gaps = {536870912, 268435456, 134217728, 67108864, 33554432, 16777216, 8388608, 4194304, 2097152, 1048576, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Powers of 2"; }
    },
    POW_OF_E {
        private final int[] gaps = {485165196, 178482302, 65659970, 24154954, 8886112, 3269018, 1202605, 442414, 162756, 59875, 22027, 8104, 2982, 1098, 404, 149, 56, 21, 8, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "round[1 + e^(n-2)]"; }
    },
    PRATT_58 {
        private final int[] gaps = {1000000000, 819200000, 671088640, 625000000, 512000000, 419430400, 390625000, 320000000, 262144000, 244140625, 200000000, 163840000, 134217728, 125000000, 102400000, 83886080, 78125000, 64000000, 52428800, 48828125, 40000000, 32768000, 25000000, 20480000, 16777216, 15625000, 12800000, 10485760, 9765625, 8000000, 6553600, 5000000, 4096000, 3125000, 2560000, 2097152, 1953125, 1600000, 1310720, 1000000, 819200, 625000, 512000, 390625, 320000, 262144, 200000, 163840, 125000, 102400, 78125, 64000, 40000, 32768, 25000, 20480, 15625, 12800, 8000, 5000, 4096, 3125, 2560, 1600, 1000, 625, 512, 320, 200, 125, 64, 40, 25, 8, 5, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Pratt5x8 (5^p * 8^q)"; }
    },
    FIBN {
        private final int[] gaps = {701408733, 433494437, 267914296, 165580141, 102334155, 63245986, 39088169, 24157817, 14930352, 9227465, 5702887, 3524578, 2178309, 1346269, 832040, 514229, 317811, 196418, 121393, 75025, 46368, 28657, 17711, 10946, 6765, 4181, 2584, 1597, 987, 610, 377, 233, 144, 89, 55, 34, 21, 13, 8, 5, 3, 2, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Fibonacci Numbers"; }
    },
    FIBSQ {
        private final int[] gaps = {821223649, 313679521, 119814916, 45765225, 17480761, 6677056, 2550409, 974169, 372100, 142129, 54289, 20736, 7921, 3025, 1156, 441, 169, 64, 25, 9, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Fibonacci Numbers Squared"; }
    },
    LEOSQ {
        private final int[] gaps = {479215881, 183033841, 69906321, 26697889, 10195249, 3892729, 1485961, 567009, 216225, 82369, 31329, 11881, 4489, 1681, 625, 225, 81, 25, 9, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Leonardo Numbers Squared"; }
    },
    FIBNP {
        private final int[] gaps = {1031612713, 217378076, 45806244, 9651787, 2034035, 428481, 90358, 19001, 4025, 836, 182, 34, 9, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "floor[fibn(k) ^ (1+\u221A5)]"; }
    },
    FIB_25 {
        private final int[] gaps = {507544127, 193864606, 74049690, 28284465, 10803704, 4126648, 1576239, 602070, 229970, 87841, 33552, 12816, 4895, 1870, 714, 273, 104, 40, 15, 6, 2, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "fib25"; }
    },
    ORLP_25 {
        private final int[] gaps = {805249025, 402612225, 201297921, 100642817, 50317313, 25155585, 12575745, 6286337, 3142145, 1570305, 784641, 391937, 195713, 97665, 48705, 24257, 12065, 5985, 2961, 1457, 713, 345, 165, 77, 35, 15, 6, 2, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "orlp25"; }
    },
    SEDGE_86B {
        private final int[] gaps = {536723465, 134144009, 33517577, 8370185, 2087945, 519689, 128777, 31625, 7625, 1769, 377, 65, 5, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "sedg86b"; }
    },
    E_248 {
        private final int[] gaps = {476361856, 192081394, 77452175, 31230716, 12593031, 5077835, 2047515, 825611, 332908, 134237, 54128, 21826, 8801, 3549, 1431, 577, 233, 94, 38, 16, 7, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "ceil[2.48 ^ (k-1)]"; }
    },
    SQRT_5 {
        private final int[] gaps = {820299269, 366848983, 164059859, 73369801, 32811973, 14673961, 6562397, 2934793, 1312481, 586961, 262495, 117391, 52501, 23479, 10499, 4693, 2099, 937, 419, 187, 83, 37, 16, 7, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "\u221A5 Co-primes"; }
    },
    LNBASE {
        private final int[] gaps = {706523501, 259915471, 95617559, 35175733, 12940429, 4760519, 1751297, 644267, 237013, 87193, 32077, 11801, 4341, 1597, 587, 215, 79, 29, 11, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Taihennami's LNBase"; }
    },
    CONVEXP {
        private final int[] gaps = {711111829, 329129569, 152022151, 70065599, 32218247, 14778563, 6761173, 3084517, 1402913, 635989, 287281, 129259, 57907, 25819, 11449, 5041, 2209, 961, 411, 175, 73, 29, 11, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Taihennami's ConvExp3"; }
    },
    TKF_25P1 {
        private final int[] gaps = {936370810, 416164804, 184962135, 82205393, 36535730, 16238102, 7216934, 3207526, 1425567, 633585, 281593, 125152, 55623, 24721, 10987, 4883, 2170, 964, 428, 190, 84, 37, 16, 7, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "F2.25+1"; }
    },
    G_236 {
        private final int[] gaps = {554152266, 234810282, 99495882, 42159272, 17864098, 7569533, 3207429, 1359080, 575881, 244017, 103397, 43812, 18564, 7866, 3333, 1412, 598, 253, 107, 45, 19, 8, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "C2.36"; }
    },
    G_23601 {
        private final int[] gaps = {554510374, 234952067, 99551742, 42181154, 17872613, 7572820, 3208686, 1359555, 576058, 244082, 103420, 43820, 18567, 7867, 3333, 1412, 598, 253, 107, 45, 19, 8, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "C2.36010"; }
    },
    G_14027 {
        private final int[] gaps = {978102099, 456999396, 213524179, 99765066, 46613308, 21779171, 10175898, 4754492, 2221444, 1037926, 484950, 226583, 105866, 49463, 23110, 10797, 5044, 2356, 1100, 513, 239, 111, 51, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "SC2.14027"; }
    },
    G_14399 {
        private final int[] gaps = {1012445118, 472224738, 220255102, 102731403, 47915989, 22348979, 10424012, 4861968, 2267719, 1057709, 493336, 230101, 107323, 50057, 23347, 10889, 5078, 2368, 1104, 514, 239, 111, 51, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "C2.14399+1"; }
    },
    C_163P1 {
        private final int[] gaps = {730725073, 137010951, 25689553, 4816791, 903148, 169340, 31751, 5953, 1116, 209, 39, 7, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "C16/3+1"; }
    },
    X_22942 {
        private final int[] gaps = {740991070, 332369437, 149083366, 66870920, 29994761, 13454065, 6034783, 2706885, 1214166, 544611, 244284, 109573, 49149, 22046, 9889, 4436, 1990, 893, 401, 167, 69, 28, 11, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "SC2.39(401) SF2.22942"; }
    },
    X_26993 {
        private final int[] gaps = {450322302, 198385987, 87397404, 38502247, 16961866, 7472419, 3291917, 1450229, 638888, 281458, 123995, 54626, 24066, 10603, 4672, 2059, 908, 401, 167, 69, 28, 11, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "SC2.39(401) SF2.26993"; }
    },
    X_22972 {
        private final int[] gaps = {685422937, 307403144, 137866255, 61831197, 27730477, 12436754, 5577721, 2501535, 1121906, 503161, 225662, 101207, 45391, 20358, 9131, 4096, 1838, 825, 371, 167, 69, 28, 11, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "aphitorite's Split Ratio"; }
    },
    TOKUOG {
        private final int[] gaps = {510097199, 226709865, 100759940, 44782195, 19903197, 8845865, 3931495, 1747330, 776590, 345151, 153400, 68177, 30300, 13466, 5984, 2659, 1181, 524, 232, 102, 45, 19, 8, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Original Tokuda"; }
    },
    TOKUDA_2 {
        private final int[] gaps = {447524607, 212837706, 94863989, 42281871, 18845471, 8399623, 3743800, 1668650, 743735, 331490, 147748, 65853, 29351, 13082, 5831, 2599, 1158, 516, 230, 102, 45, 20, 9, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Improved Tokuda"; }
    },
    PCIURA {
        private final int[] gaps = {765415766, 322071379, 135521083, 57024514, 23994755, 10096504, 4248404, 1787642, 752203, 316512, 133182, 56040, 23580, 9922, 4175, 1757, 739, 311, 131, 55, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Pseudo Ciura"; }
    },
    BLAAZEN {
        private final int[] gaps = {538555487, 242592563, 109275931, 49223393, 22172701, 9987709, 4498951, 2026567, 912871, 411211, 185267, 83459, 37957, 17099, 7703, 3463, 1559, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Blazzen's Extended Ciura (with Primes)"; }
    },
    CURA_250 {
        private final int[] gaps = {754868335, 335497038, 149109795, 66271020, 29453787, 13090572, 5818032, 2585792, 1149241, 510774, 227011, 100894, 44842, 19930, 8858, 3937, 1750, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Ciura(1750) F2.25"; }
    },
    CURA_246 {
        private final int[] gaps = {733650877, 326647764, 145435336, 64753044, 28830385, 12836325, 5715194, 2544610, 1132952, 504431, 224591, 99996, 44522, 19823, 8826, 3930, 1750, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Ciura(1750) F2.246"; }
    },
    C_24603 {
        private final int[] gaps = {733777756, 326699891, 145456602, 64761647, 28833830, 12837687, 5715724, 2544812, 1133027, 504458, 224600, 99999, 44523, 19823, 8826, 3930, 1750, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Ciura(1750) F2.24603"; }
    },
    C_1636 {
        private final int[] gaps = {631316523, 282544094, 126451886, 56593218, 25328150, 11335549, 5073196, 2270496, 1016155, 454778, 203535, 91092, 40768, 18246, 8166, 3655, 1636, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Ciura(1636) F2.2344"; }
    },
    CURALDE {
        private final int[] gaps = {631315018, 282544198, 126451290, 56518561, 25328324, 11335582, 5073398, 2270499, 1016156, 454741, 203519, 91064, 40764, 18235, 8172, 3657, 1636, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Ciura(1636) F2.2344 (LDE Improved)"; }
    },
    MCH_1504 {
        private final int[] gaps = {859139840, 386999928, 174324292, 78524456, 35371377, 15933053, 7434649, 3446017, 1599893, 745919, 347077, 162005, 74428, 34644, 15948, 7196, 3263, 1504, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Machoota's Ciura+1504 Extended"; }
    },
    MCH_1541 {
        private final int[] gaps = {1066699439, 480495243, 216439299, 97495180, 43916748, 19782319, 9002887, 4081849, 1883473, 858419, 392867, 179433, 81907, 37835, 17041, 7699, 3498, 1541, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Machoota's Ciura+1541 Extended"; }
    },
    MCH_1636 {
        private final int[] gaps = {1247501165, 561937462, 253124983, 114020263, 51360479, 23135351, 10528127, 4697153, 2131981, 973657, 443557, 197803, 89129, 40354, 18118, 8129, 3659, 1636, 701, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Machoota's Ciura+1636 Extended"; }
    },
    MACHOOTA_BEST {
        private final int[] thresholds = {16, 32, 64, 128, 256, 512, 1000, 2000, 3000, 5000, 10000, 20000, 30000, 50000, 100000, 1000000, 10000000, 100000000, 1000000000};
        private final int[][] sequences = {
            {14, 5, 1},
            {13, 4, 1},
            {62, 38, 9, 4, 1},
            {85, 24, 9, 4, 1},
            {238, 89, 27, 10, 4, 1},
            {483, 189, 57, 23, 10, 4, 1},
            {996, 409, 156, 57, 23, 10, 4, 1},
            {1979, 1208, 347, 132, 57, 23, 10, 4, 1},
            {2778, 1044, 313, 132, 57, 23, 10, 4, 1},
            {4921, 1937, 701, 301, 132, 57, 23, 10, 4, 1},
            {9941, 6085, 1733, 701, 301, 132, 57, 23, 10, 4, 1},
            {19908, 13293, 4021, 1636, 701, 301, 132, 57, 23, 10, 4, 1},
            {28631, 11336, 3498, 1541, 701, 301, 132, 57, 23, 10, 4, 1},
            {49256, 30113, 8399, 3263, 1504, 701, 301, 132, 57, 23, 10, 4, 1},
            {99668, 62185, 17736, 6913, 3165, 1445, 644, 301, 132, 57, 23, 10, 4, 1},
            {989292, 606250, 171523, 71651, 32910, 14917, 6847, 3227, 1408, 644, 301, 132, 57, 23, 10, 4, 1},
            {9775485, 5934785, 1645254, 692843, 324011, 149728, 69487, 31970, 14842, 6847, 3227, 1408, 644, 301, 132, 57, 23, 10, 4, 1},
            {98743101, 60001006, 17234807, 6662519, 3085219, 1442593, 667787, 316034, 147869, 68467, 31970, 14842, 6847, 3227, 1408, 644, 301, 132, 57, 23, 10, 4, 1},
            {981186611, 600000000, 167899094, 66950617, 30994463, 14349443, 6662519, 3085219, 1442593, 667787, 316034, 147869, 68467, 31970, 14842, 6847, 3227, 1408, 644, 301, 132, 57, 23, 10, 4, 1}
        };
        @Override
        public int[] getGaps(int n) {
            for (int i = 0; i < thresholds.length; i++) {
                if (n <= thresholds[i]) {
                    return sequences[i];
                }
            }
            return sequences[sequences.length - 1];
        }
        @Override
        public String getName() { return "Machoota's Best"; }
    },
    MCH_BEST_WORST {
        private final int[] thresholds = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 64, 91, 128};
        private final int[][] sequences = {
            {4, 1},
            {6, 4, 1},
            {7, 5, 1},
            {8, 5, 1},
            {9, 6, 1},
            {10, 9, 6, 1},
            {11, 10, 7, 1},
            {10, 6, 1},
            {13, 11, 7, 1},
            {14, 9, 4, 1},
            {9, 7, 4, 1},
            {9, 6, 4, 1},
            {17, 9, 6, 4, 1},
            {9, 7, 4, 1},
            {9, 7, 4, 1},
            {9, 7, 4, 1},
            {9, 7, 4, 1},
            {12, 7, 3, 1},
            {9, 7, 4, 1},
            {24, 9, 7, 4, 1},
            {9, 7, 4, 1},
            {26, 9, 7, 4, 1},
            {9, 7, 4, 1},
            {9, 7, 4, 1},
            {29, 9, 7, 4, 1},
            {9, 7, 4, 1},
            {9, 7, 4, 1},
            {32, 9, 7, 4, 1},
            {33, 9, 7, 4, 1},
            {33, 9, 7, 4, 1},
            {34, 19, 9, 7, 4, 1},
            {35, 9, 7, 4, 1},
            {35, 21, 11, 9, 4, 1},
            {38, 21, 11, 9, 4, 1},
            {28, 9, 7, 4, 1},
            {40, 28, 9, 7, 4, 1},
            {11, 9, 4, 1},
            {41, 21, 11, 9, 4, 1},
            {21, 11, 9, 4, 1},
            {21, 11, 9, 4, 1},
            {32, 11, 9, 4, 1},
            {44, 30, 11, 9, 4, 1},
            {59, 36, 30, 11, 9, 4, 1}
        };
        @Override
        public int[] getGaps(int n) {
            for (int i = 0; i < thresholds.length; i++) {
                if (n <= thresholds[i]) {
                    return sequences[i];
                }
            }
            return sequences[sequences.length - 1];
        }
        @Override
        public String getName() { return "Machoota's Best Worst-Case"; }
        @Override
        public int getLimit() { return 128; }
    },
    PRIMES_2 {
        private final int[] gaps = {842879579, 421439783, 210719881, 105359939, 52679969, 26339969, 13169977, 6584983, 3292489, 1646237, 823117, 411527, 205759, 102877, 51437, 25717, 12853, 6421, 3203, 1597, 797, 397, 197, 97, 47, 23, 11, 5, 2, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Prime > 2x Last Gap"; }
    },
    PRIMES_3 {
        private final int[] gaps = {560615723, 186871907, 62290633, 20763541, 6921179, 2307059, 769019, 256337, 85439, 28477, 9491, 3163, 1049, 347, 113, 37, 11, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Prime > 3x Last Gap"; }
    },
    SEDG_82M {
        private final int[] gaps = {268460033, 67121153, 16783361, 4197377, 1050113, 262913, 65921, 16577, 4193, 1073, 281, 77, 23, 8, 3, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Sedgewick's Powers of 4 (Modified)"; }
    },
    KOR {
        private final int[] gaps = {410151271, 157840433, 58548857, 21521774, 8810089, 3501671, 1355339, 543749, 213331, 84801, 27901, 11969, 4711, 1968, 815, 367, 145, 62, 23, 12, 5, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Korean Wiki"; }
    },

    // Finite gap sequences
    PCBOY_F {
        private final int[] gaps = {100000, 40231, 15022, 6394, 3103, 1428, 644, 301, 132, 57, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "PCBoy's Final"; }
        @Override
        public int getLimit() { return gaps[0]; }
    },
    SEJ_B {
        private final int[] gaps = {187, 72, 27, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "SEJ-B"; }
        @Override
        public int getLimit() { return gaps[0]; }
    },
    STOVR_7 {
        private final int[] gaps = {499871, 494198, 451488, 128823, 35957, 13353, 5467, 2673, 1097, 340, 171, 58, 24, 9, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Stover 7"; }
        @Override
        public int getLimit() { return 500000; }
    },
    STOVR_8 {
        private final int[] gaps = {498201, 461299, 275360, 62025, 18168, 8186, 3716, 1325, 444, 177, 61, 23, 13, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Stover 8"; }
        @Override
        public int getLimit() { return 500000; }
    },
    GBY_1 {
        private final int[] gaps = {5885, 2674, 1215, 552, 250, 113, 51, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Best GBY"; }
        @Override
        public int getLimit() { return 10000; }
    },
    GBY_2 {
        private final int[] gaps = {303573, 137987, 62721, 28509, 12958, 5889, 2676, 1216, 552, 250, 113, 51, 23, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Best GBY"; }
        @Override
        public int getLimit() { return 667000; }
    },
    BESTSQ {
        private final int[] gaps = {2304, 961, 400, 196, 81, 25, 10, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "Best Squares"; }
        @Override
        public int getLimit() { return 5000; }
    },
    GC_282 {
        private final int[] gaps = {9029, 3956, 1733, 759, 332, 145, 63, 27, 11, 4, 1};
        @Override
        public int[] getGaps(int n) { return gaps; }
        @Override
        public String getName() { return "GC282"; }
        @Override
        public int getLimit() { return gaps[0]; }
    };

    public static final ShellsortGaps DEFAULT = MCH_1636;

    public abstract String getName();
    public abstract int[] getGaps(int n);
    public int getLimit() { return 0; }

}
