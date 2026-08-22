package com.example.shendet.data

/**
 * Database of E-codes (EU food additives) and INS codes (International Numbering System)
 * These are used to identify food additives in ingredient lists
 */
object ECodesDatabase {
    
    data class ECode(
        val code: String,
        val name: String,
        val nameAlbanian: String,
        val category: String,
        val isHarmful: Boolean,
        val description: String,
        val healthRisk: String = ""
    )
    
    val eCodes = mapOf(
        // Colorants (E100-E199)
        "E100" to ECode("E100", "Curcumin", "Kurkumin", "Ngjyrues", false, "Ngjyrë e verdhë natyrale nga kurkuma", "I sigurt"),
        "E101" to ECode("E101", "Riboflavin", "Riboflavin", "Ngjyrues", false, "Vitaminë B2", "I sigurt"),
        "E102" to ECode("E102", "Tartrazine", "Tartrazinë", "Ngjyrues", true, "Ngjyrë artificiale e verdhë", "Mund të shkaktojë alergji, hiperaktivitet te fëmijët"),
        "E104" to ECode("E104", "Quinoline Yellow", "E verdhë kinoline", "Ngjyrues", true, "Ngjyrë artificiale", "Mund të shkaktojë alergji"),
        "E110" to ECode("E110", "Sunset Yellow FCF", "E verdhë perëndimi dielli", "Ngjyrues", true, "Ngjyrë artificiale portokalli", "Mund të shkaktojë alergji, hiperaktivitet"),
        "E120" to ECode("E120", "Cochineal, Carminic acid", "Kochenil, Acid karminik", "Ngjyrues", false, "Ngjyrë e kuqe natyrale nga insektet", "I sigurt, por jo vegan"),
        "E122" to ECode("E122", "Azorubine", "Azorubine", "Ngjyrues", true, "Ngjyrë artificiale e kuqe", "Mund të shkaktojë alergji, hiperaktivitet"),
        "E123" to ECode("E123", "Amaranth", "Amarant", "Ngjyrues", true, "Ngjyrë artificiale e kuqe", "I ndaluar në SHBA, mund të shkaktojë kancer"),
        "E124" to ECode("E124", "Ponceau 4R", "Ponceau 4R", "Ngjyrues", true, "Ngjyrë artificiale e kuqe", "Mund të shkaktojë alergji"),
        "E127" to ECode("E127", "Erythrosine", "Eritrozinë", "Ngjyrues", true, "Ngjyrë artificiale e kuqe", "Mund të ndikojë në tiroid"),
        "E129" to ECode("E129", "Allura Red AC", "E kuqe Allura", "Ngjyrues", true, "Ngjyrë artificiale e kuqe", "Mund të shkaktojë alergji, hiperaktivitet"),
        "E131" to ECode("E131", "Patent Blue V", "Blu patent V", "Ngjyrues", true, "Ngjyrë artificiale blu", "Mund të shkaktojë alergji"),
        "E132" to ECode("E132", "Indigo Carmine", "Indigo Karmin", "Ngjyrues", false, "Ngjyrë blu", "I sigurt në sasi të vogla"),
        "E133" to ECode("E133", "Brilliant Blue FCF", "Blu e ndritshme", "Ngjyrues", true, "Ngjyrë artificiale blu", "Mund të shkaktojë alergji"),
        "E140" to ECode("E140", "Chlorophylls", "Klorofil", "Ngjyrues", false, "Ngjyrë e gjelbër natyrale", "I sigurt"),
        "E141" to ECode("E141", "Chlorophyllin", "Klorofilinë", "Ngjyrues", false, "Derivat i klorofilit", "I sigurt"),
        "E150a" to ECode("E150a", "Plain caramel", "Karamel i thjeshtë", "Ngjyrues", false, "Ngjyrë kafe natyrale", "I sigurt"),
        "E150b" to ECode("E150b", "Caustic sulphite caramel", "Karamel sulfit", "Ngjyrues", true, "Ngjyrë kafe e përpunuar", "Mund të përmbajë 4-MEI"),
        "E150c" to ECode("E150c", "Ammonia caramel", "Karamel amoniak", "Ngjyrues", true, "Ngjyrë kafe me amoniak", "Mund të përmbajë 4-MEI, kancerogjen"),
        "E150d" to ECode("E150d", "Sulphite ammonia caramel", "Karamel sulfit amoniak", "Ngjyrues", true, "Ngjyrë kafe e përpunuar", "Mund të përmbajë 4-MEI"),
        "E151" to ECode("E151", "Brilliant Black BN", "E zezë e ndritshme", "Ngjyrues", true, "Ngjyrë artificiale e zezë", "Mund të shkaktojë alergji"),
        "E153" to ECode("E153", "Vegetable carbon", "Karbon bimeor", "Ngjyrues", false, "Ngjyrë e zezë natyrale", "I sigurt"),
        "E155" to ECode("E155", "Brown HT", "Kafe HT", "Ngjyrues", true, "Ngjyrë artificiale kafe", "I ndaluar në disa vende"),
        "E160a" to ECode("E160a", "Beta-carotene", "Beta-karoten", "Ngjyrues", false, "Ngjyrë portokalli, antioksidant", "I sigurt"),
        "E160b" to ECode("E160b", "Annatto", "Anato", "Ngjyrues", false, "Ngjyrë e verdhë natyrale", "I sigurt"),
        "E160c" to ECode("E160c", "Paprika extract", "Ekstrakt paprike", "Ngjyrues", false, "Ngjyrë e kuqe natyrale", "I sigurt"),
        "E160d" to ECode("E160d", "Lycopene", "Likopen", "Ngjyrues", false, "Ngjyrë e kuqe, antioksidant", "I sigurt"),
        "E161b" to ECode("E161b", "Lutein", "Luteinë", "Ngjyrues", false, "Ngjyrë e verdhë, antioksidant", "I sigurt"),
        "E162" to ECode("E162", "Beetroot Red", "E kuqe panxhari", "Ngjyrues", false, "Ngjyrë e kuqe natyrale", "I sigurt"),
        "E163" to ECode("E163", "Anthocyanins", "Antocianina", "Ngjyrues", false, "Ngjyrë natyrale blu/vjollcë", "I sigurt"),
        "E170" to ECode("E170", "Calcium carbonate", "Karbonat kalciumi", "Ngjyrues", false, "Ngjyrë e bardhë natyrale", "I sigurt"),
        "E171" to ECode("E171", "Titanium dioxide", "Dioksid titani", "Ngjyrues", true, "Ngjyrë e bardhë", "I ndaluar në EU si shtesë ushqimore (2022)"),
        "E172" to ECode("E172", "Iron oxides", "Okside hekuri", "Ngjyrues", false, "Ngjyrë natyrale kafe/e kuqe", "I sigurt"),
        
        // Preservatives (E200-E299)
        "E200" to ECode("E200", "Sorbic acid", "Acid sorbik", "Konservant", false, "Konservant natyral", "I sigurt"),
        "E201" to ECode("E201", "Sodium sorbate", "Sorbat natriumi", "Konservant", false, "Konservant", "I sigurt"),
        "E202" to ECode("E202", "Potassium sorbate", "Sorbat kaliumi", "Konservant", false, "Konservant i zakonshëm", "I sigurt"),
        "E203" to ECode("E203", "Calcium sorbate", "Sorbat kalciumi", "Konservant", false, "Konservant", "I sigurt"),
        "E210" to ECode("E210", "Benzoic acid", "Acid benzoik", "Konservant", true, "Konservant artificial", "Mund të shkaktojë alergji"),
        "E211" to ECode("E211", "Sodium benzoate", "Benzoat natriumi", "Konservant", true, "Konservant artificial", "Mund të formojë benzen kancerogjen me vitaminë C"),
        "E212" to ECode("E212", "Potassium benzoate", "Benzoat kaliumi", "Konservant", true, "Konservant artificial", "Mund të shkaktojë alergji"),
        "E213" to ECode("E213", "Calcium benzoate", "Benzoat kalciumi", "Konservant", true, "Konservant artificial", "Mund të shkaktojë alergji"),
        "E214" to ECode("E214", "Ethyl p-hydroxybenzoate", "Etil paraben", "Konservant", true, "Paraben konservant", "Mund të ndikojë në hormone"),
        "E215" to ECode("E215", "Ethyl p-hydroxybenzoate sodium salt", "Etil paraben natrium", "Konservant", true, "Paraben konservant", "Mund të ndikojë në hormone"),
        "E216" to ECode("E216", "Propyl p-hydroxybenzoate", "Propil paraben", "Konservant", true, "Paraben konservant", "I ndaluar, mund të ndikojë në hormone"),
        "E217" to ECode("E217", "Propyl p-hydroxybenzoate sodium salt", "Propil paraben natrium", "Konservant", true, "Paraben konservant", "I ndaluar, mund të ndikojë në hormone"),
        "E218" to ECode("E218", "Methyl p-hydroxybenzoate", "Metil paraben", "Konservant", true, "Paraben konservant", "Mund të ndikojë në hormone"),
        "E219" to ECode("E219", "Methyl p-hydroxybenzoate sodium salt", "Metil paraben natrium", "Konservant", true, "Paraben konservant", "Mund të ndikojë në hormone"),
        "E220" to ECode("E220", "Sulphur dioxide", "Dioksid squfuri", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji, astmë"),
        "E221" to ECode("E221", "Sodium sulphite", "Sulfit natriumi", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji"),
        "E222" to ECode("E222", "Sodium hydrogen sulphite", "Bisulfit natriumi", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji"),
        "E223" to ECode("E223", "Sodium metabisulphite", "Metabisulfit natriumi", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji"),
        "E224" to ECode("E224", "Potassium metabisulphite", "Metabisulfit kaliumi", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji"),
        "E226" to ECode("E226", "Calcium sulphite", "Sulfit kalciumi", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji"),
        "E227" to ECode("E227", "Calcium hydrogen sulphite", "Bisulfit kalciumi", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji"),
        "E228" to ECode("E228", "Potassium hydrogen sulphite", "Bisulfit kaliumi", "Konservant", true, "Konservant me squfur", "Mund të shkaktojë alergji"),
        "E230" to ECode("E230", "Biphenyl", "Bifenil", "Konservant", true, "Fungicid", "I ndaluar në disa vende, kancerogjen"),
        "E231" to ECode("E231", "Orthophenyl phenol", "Ortofenil fenol", "Konservant", true, "Fungicid", "Mund të shkaktojë dëmtime të mëlçisë"),
        "E232" to ECode("E232", "Orthophenyl phenol sodium salt", "Ortofenil fenol natrium", "Konservant", true, "Fungicid", "Mund të shkaktojë dëmtime të mëlçisë"),
        "E234" to ECode("E234", "Nisin", "Nisin", "Konservant", false, "Konservant natyral nga baktere", "I sigurt"),
        "E235" to ECode("E235", "Natamycin", "Natamicinë", "Konservant", false, "Antifungal natyral", "I sigurt"),
        "E239" to ECode("E239", "Hexamethylene tetramine", "Heksamilen tetraminë", "Konservant", true, "Konservant artificial", "Mund të lëshojë formaldehid"),
        "E242" to ECode("E242", "Dimethyl dicarbonate", "Dimetil dikarbonat", "Konservant", false, "Konservant për pije", "I sigurt"),
        "E249" to ECode("E249", "Potassium nitrite", "Nitrit kaliumi", "Konservant", true, "Konservant për mish", "Mund të formojë nitrosamina kancerogjene"),
        "E250" to ECode("E250", "Sodium nitrite", "Nitrit natriumi", "Konservant", true, "Konservant për mish", "Mund të formojë nitrosamina kancerogjene"),
        "E251" to ECode("E251", "Sodium nitrate", "Nitrat natriumi", "Konservant", true, "Konservant për mish", "Mund të formojë nitrosamina kancerogjene"),
        "E252" to ECode("E252", "Potassium nitrate", "Nitrat kaliumi", "Konservant", true, "Konservant për mish", "Mund të formojë nitrosamina kancerogjene"),
        "E260" to ECode("E260", "Acetic acid", "Acid acetik", "Konservant", false, "Uthull", "I sigurt"),
        "E261" to ECode("E261", "Potassium acetates", "Acetat kaliumi", "Konservant", false, "Konservant", "I sigurt"),
        "E262" to ECode("E262", "Sodium acetates", "Acetat natriumi", "Konservant", false, "Konservant", "I sigurt"),
        "E263" to ECode("E263", "Calcium acetate", "Acetat kalciumi", "Konservant", false, "Konservant", "I sigurt"),
        "E270" to ECode("E270", "Lactic acid", "Acid laktik", "Konservant", false, "Acid natyral", "I sigurt"),
        "E280" to ECode("E280", "Propionic acid", "Acid propionik", "Konservant", false, "Konservant natyral", "I sigurt"),
        "E281" to ECode("E281", "Sodium propionate", "Propionat natriumi", "Konservant", false, "Konservant", "I sigurt"),
        "E282" to ECode("E282", "Calcium propionate", "Propionat kalciumi", "Konservant", false, "Konservant për bukë", "I sigurt"),
        "E283" to ECode("E283", "Potassium propionate", "Propionat kaliumi", "Konservant", false, "Konservant", "I sigurt"),
        "E284" to ECode("E284", "Boric acid", "Acid borik", "Konservant", true, "Konservant", "I kufizuar, mund të jetë toksik"),
        "E285" to ECode("E285", "Sodium tetraborate", "Tetraborat natriumi", "Konservant", true, "Boraks", "I kufizuar, mund të jetë toksik"),
        "E290" to ECode("E290", "Carbon dioxide", "Dioksid karboni", "Konservant", false, "Gas natyral", "I sigurt"),
        "E296" to ECode("E296", "Malic acid", "Acid malik", "Konservant", false, "Acid natyral nga mollët", "I sigurt"),
        
        // Antioxidants (E300-E399)
        "E300" to ECode("E300", "Ascorbic acid", "Acid askorbik", "Antioksidant", false, "Vitaminë C", "I sigurt"),
        "E301" to ECode("E301", "Sodium ascorbate", "Askorbat natriumi", "Antioksidant", false, "Formë e vitaminës C", "I sigurt"),
        "E302" to ECode("E302", "Calcium ascorbate", "Askorbat kalciumi", "Antioksidant", false, "Formë e vitaminës C", "I sigurt"),
        "E303" to ECode("E303", "Potassium ascorbate", "Askorbat kaliumi", "Antioksidant", false, "Formë e vitaminës C", "I sigurt"),
        "E304" to ECode("E304", "Fatty acid esters of ascorbic acid", "Esterë të acidit askorbik", "Antioksidant", false, "Antioksidant", "I sigurt"),
        "E306" to ECode("E306", "Tocopherol-rich extract", "Ekstrakt tokoferoli", "Antioksidant", false, "Vitaminë E natyrale", "I sigurt"),
        "E307" to ECode("E307", "Alpha-tocopherol", "Alfa-tokoferol", "Antioksidant", false, "Vitaminë E", "I sigurt"),
        "E308" to ECode("E308", "Gamma-tocopherol", "Gama-tokoferol", "Antioksidant", false, "Vitaminë E", "I sigurt"),
        "E309" to ECode("E309", "Delta-tocopherol", "Delta-tokoferol", "Antioksidant", false, "Vitaminë E", "I sigurt"),
        "E310" to ECode("E310", "Propyl gallate", "Gallat propili", "Antioksidant", true, "Antioksidant artificial", "Mund të shkaktojë alergji"),
        "E311" to ECode("E311", "Octyl gallate", "Gallat oktili", "Antioksidant", true, "Antioksidant artificial", "Mund të shkaktojë alergji"),
        "E312" to ECode("E312", "Dodecyl gallate", "Gallat dodecili", "Antioksidant", true, "Antioksidant artificial", "Mund të shkaktojë alergji"),
        "E315" to ECode("E315", "Erythorbic acid", "Acid eritorbik", "Antioksidant", false, "Antioksidant", "I sigurt"),
        "E316" to ECode("E316", "Sodium erythorbate", "Eritorbat natriumi", "Antioksidant", false, "Antioksidant", "I sigurt"),
        "E319" to ECode("E319", "Tert-Butylhydroquinone (TBHQ)", "TBHQ", "Antioksidant", true, "Antioksidant artificial", "Mund të shkaktojë probleme stomaku"),
        "E320" to ECode("E320", "Butylated hydroxyanisole (BHA)", "BHA", "Antioksidant", true, "Antioksidant artificial", "I dyshimtë si kancerogjen"),
        "E321" to ECode("E321", "Butylated hydroxytoluene (BHT)", "BHT", "Antioksidant", true, "Antioksidant artificial", "I dyshimtë si kancerogjen"),
        "E322" to ECode("E322", "Lecithins", "Lecitina", "Antioksidant", false, "Emulsifikues natyral", "I sigurt"),
        "E325" to ECode("E325", "Sodium lactate", "Laktat natriumi", "Antioksidant", false, "Antioksidant", "I sigurt"),
        "E326" to ECode("E326", "Potassium lactate", "Laktat kaliumi", "Antioksidant", false, "Antioksidant", "I sigurt"),
        "E327" to ECode("E327", "Calcium lactate", "Laktat kalciumi", "Antioksidant", false, "Antioksidant", "I sigurt"),
        "E330" to ECode("E330", "Citric acid", "Acid citrik", "Antioksidant", false, "Acid natyral nga limoni", "I sigurt"),
        "E331" to ECode("E331", "Sodium citrates", "Citrat natriumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E332" to ECode("E332", "Potassium citrates", "Citrat kaliumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E333" to ECode("E333", "Calcium citrates", "Citrat kalciumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E334" to ECode("E334", "Tartaric acid", "Acid tartarik", "Antioksidant", false, "Acid natyral", "I sigurt"),
        "E335" to ECode("E335", "Sodium tartrates", "Tartrat natriumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E336" to ECode("E336", "Potassium tartrates", "Tartrat kaliumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E337" to ECode("E337", "Sodium potassium tartrate", "Tartrat natrium kalium", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E338" to ECode("E338", "Phosphoric acid", "Acid fosforik", "Antioksidant", true, "Acid artificial", "Mund të shkaktojë probleme me kockat"),
        "E339" to ECode("E339", "Sodium phosphates", "Fosfat natriumi", "Antioksidant", true, "Rregullues aciditeti", "Mund të shkaktojë probleme me veshkat"),
        "E340" to ECode("E340", "Potassium phosphates", "Fosfat kaliumi", "Antioksidant", true, "Rregullues aciditeti", "Mund të shkaktojë probleme me veshkat"),
        "E341" to ECode("E341", "Calcium phosphates", "Fosfat kalciumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E343" to ECode("E343", "Magnesium phosphates", "Fosfat magnezi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E350" to ECode("E350", "Sodium malates", "Malat natriumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E351" to ECode("E351", "Potassium malate", "Malat kaliumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E352" to ECode("E352", "Calcium malates", "Malat kalciumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E353" to ECode("E353", "Metatartaric acid", "Acid metartarik", "Antioksidant", false, "Acid natyral", "I sigurt"),
        "E354" to ECode("E354", "Calcium tartrate", "Tartrat kalciumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E355" to ECode("E355", "Adipic acid", "Acid adipik", "Antioksidant", false, "Acid natyral", "I sigurt"),
        "E356" to ECode("E356", "Sodium adipate", "Adipat natriumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E357" to ECode("E357", "Potassium adipate", "Adipat kaliumi", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E363" to ECode("E363", "Succinic acid", "Acid sukcinik", "Antioksidant", false, "Acid natyral", "I sigurt"),
        "E380" to ECode("E380", "Triammonium citrate", "Citrat triamonium", "Antioksidant", false, "Rregullues aciditeti", "I sigurt"),
        "E385" to ECode("E385", "Calcium disodium EDTA", "EDTA kalcium dinatrium", "Antioksidant", true, "Kelating agent", "Mund të lidhë mineralet"),
        
        // Emulsifiers (E400-E499)
        "E400" to ECode("E400", "Alginic acid", "Acid alginik", "Emulsifikues", false, "Trashësues natyral nga algat", "I sigurt"),
        "E401" to ECode("E401", "Sodium alginate", "Alginate natriumi", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E402" to ECode("E402", "Potassium alginate", "Alginate kaliumi", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E403" to ECode("E403", "Ammonium alginate", "Alginate amoniumi", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E404" to ECode("E404", "Calcium alginate", "Alginate kalciumi", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E405" to ECode("E405", "Propylene glycol alginate", "Alginate propilen glikol", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E406" to ECode("E406", "Agar", "Agar", "Emulsifikues", false, "Xhel natyral nga algat", "I sigurt"),
        "E407" to ECode("E407", "Carrageenan", "Karragjenan", "Emulsifikues", true, "Xhel nga algat deti", "Mund të shkaktojë inflamacion, probleme stomaku"),
        "E407a" to ECode("E407a", "Processed euchema seaweed", "Xhel i përpunuar", "Emulsifikues", true, "Xhel nga algat", "Mund të shkaktojë probleme stomaku"),
        "E410" to ECode("E410", "Locust bean gum", "Xhame bishtajash", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E412" to ECode("E412", "Guar gum", "Xhame guar", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E413" to ECode("E413", "Tragacanth", "Tragant", "Emulsifikues", false, "Xhel natyral", "I sigurt"),
        "E414" to ECode("E414", "Acacia gum", "Xhame akacie", "Emulsifikues", false, "Xhel natyral", "I sigurt"),
        "E415" to ECode("E415", "Xanthan gum", "Xhame xantan", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E416" to ECode("E416", "Karaya gum", "Xhame karaja", "Emulsifikues", false, "Xhel natyral", "I sigurt"),
        "E417" to ECode("E417", "Tara gum", "Xhame tara", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E418" to ECode("E418", "Gellan gum", "Xhame gellan", "Emulsifikues", false, "Trashësues natyral", "I sigurt"),
        "E420" to ECode("E420", "Sorbitol", "Sorbitol", "Emulsifikues", false, "Ëmbëltues natyral", "I sigurt, por mund të shkaktojë diarre"),
        "E421" to ECode("E421", "Mannitol", "Mannitol", "Emulsifikues", false, "Ëmbëltues natyral", "I sigurt, por mund të shkaktojë diarre"),
        "E422" to ECode("E422", "Glycerol", "Glicerol", "Emulsifikues", false, "Nga yndyrnat natyrale", "I sigurt"),
        "E430" to ECode("E430", "Polyoxyethylene (8) stearate", "Polietilen oksid", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë papastërti"),
        "E431" to ECode("E431", "Polyoxyethylene (40) stearate", "Polietilen oksid", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë papastërti"),
        "E432" to ECode("E432", "Polyoxyethylene sorbitan monolaurate", "Polisorbat 20", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë papastërti"),
        "E433" to ECode("E433", "Polyoxyethylene sorbitan monooleate", "Polisorbat 80", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë papastërti"),
        "E434" to ECode("E434", "Polyoxyethylene sorbitan monopalmitate", "Polisorbat 40", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë papastërti"),
        "E435" to ECode("E435", "Polyoxyethylene sorbitan monostearate", "Polisorbat 60", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë papastërti"),
        "E436" to ECode("E436", "Polyoxyethylene sorbitan tristearate", "Polisorbat 65", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë papastërti"),
        "E440" to ECode("E440", "Pectins", "Pektina", "Emulsifikues", false, "Xhel natyral nga frutat", "I sigurt"),
        "E442" to ECode("E442", "Ammonium phosphatides", "Fosfatide amoniumi", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E444" to ECode("E444", "Sucrose acetate isobutyrate", "Acetat sukroze", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E445" to ECode("E445", "Glycerol esters of wood rosin", "Esterë gliceroli", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E450" to ECode("E450", "Diphosphates", "Difosfate", "Emulsifikues", true, "Emulsifikues artificial", "Mund të shkaktojë probleme me veshkat"),
        "E451" to ECode("E451", "Triphosphates", "Trifosfate", "Emulsifikues", true, "Emulsifikues artificial", "Mund të shkaktojë probleme me veshkat"),
        "E452" to ECode("E452", "Polyphosphates", "Polifosfate", "Emulsifikues", true, "Emulsifikues artificial", "Mund të shkaktojë probleme me veshkat"),
        "E460" to ECode("E460", "Cellulose", "Celulozë", "Emulsifikues", false, "Fibra natyrale", "I sigurt"),
        "E461" to ECode("E461", "Methyl cellulose", "Metil celulozë", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E462" to ECode("E462", "Ethyl cellulose", "Etil celulozë", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E463" to ECode("E463", "Hydroxypropyl cellulose", "Hidroksipropil celulozë", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E464" to ECode("E464", "Hydroxypropylmethyl cellulose", "Hidroksipropilmetil celulozë", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E465" to ECode("E465", "Ethyl methyl cellulose", "Etil metil celulozë", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E466" to ECode("E466", "Carboxymethyl cellulose", "Karboksimetil celulozë", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E468" to ECode("E468", "Crosslinked sodium carboxymethyl cellulose", "CMC e kryqëzuar", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E469" to ECode("E469", "Enzymatically hydrolysed carboxymethyl cellulose", "CMC e hidrolizuar", "Emulsifikues", false, "Trashësues", "I sigurt"),
        "E470a" to ECode("E470a", "Salts of fatty acids", "Kripëra acidësh yndyror", "Emulsifikues", false, "Emulsifikues natyral", "I sigurt"),
        "E470b" to ECode("E470b", "Magnesium salts of fatty acids", "Kripëra magnezi", "Emulsifikues", false, "Emulsifikues natyral", "I sigurt"),
        "E471" to ECode("E471", "Mono- and diglycerides of fatty acids", "Mono dhe digliceride", "Emulsifikues", false, "Emulsifikues natyral", "I sigurt"),
        "E472a" to ECode("E472a", "Acetic acid esters of mono- and diglycerides", "Esterë acetik", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E472b" to ECode("E472b", "Lactic acid esters of mono- and diglycerides", "Esterë laktik", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E472c" to ECode("E472c", "Citric acid esters of mono- and diglycerides", "Esterë citrik", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E472d" to ECode("E472d", "Mono- and diacetyl tartaric acid esters", "Esterë tartarik", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E472e" to ECode("E472e", "Mixed acetic and tartaric acid esters", "Esterë të përzier", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E472f" to ECode("E472f", "Sucrose esters of fatty acids", "Esterë sukroze", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E473" to ECode("E473", "Sucrose esters of fatty acids", "Esterë sukroze", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E474" to ECode("E474", "Sucroglycerides", "Sukrogliceride", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E475" to ECode("E475", "Polyglycerol esters of fatty acids", "Esterë poligliceroli", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E476" to ECode("E476", "Polyglycerol polyricinoleate", "Poliglicerol poliricinoleat", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E477" to ECode("E477", "Propan-1,2-diol esters of fatty acids", "Esterë propilen glikoli", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E478" to ECode("E478", "Lactylated fatty acid esters of glycerol and propane-1,2-diol", "Esterë të laktizuar", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E479a" to ECode("E479a", "Thermally oxidized soya bean oil", "Vaj soje i oksiduar", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë përbërës të dëmshëm"),
        "E479b" to ECode("E479b", "Thermally oxidized soya bean oil interacted with mono- and diglycerides", "Vaj soje i oksiduar", "Emulsifikues", true, "Emulsifikues artificial", "Mund të përmbajë përbërës të dëmshëm"),
        "E481" to ECode("E481", "Sodium stearoyl lactylate", "Stearoil laktat natriumi", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E482" to ECode("E482", "Calcium stearoyl lactylate", "Stearoil laktat kalciumi", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E483" to ECode("E483", "Stearyl tartrate", "Stearyl tartrat", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E491" to ECode("E491", "Sorbitan monostearate", "Sorbitan monostearat", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E492" to ECode("E492", "Sorbitan tristearate", "Sorbitan tristearat", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E493" to ECode("E493", "Sorbitan monolaurate", "Sorbitan monolaurat", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E494" to ECode("E494", "Sorbitan monooleate", "Sorbitan monooleat", "Emulsifikues", false, "Emulsifikues", "I sigurt"),
        "E495" to ECode("E495", "Sorbitan monopalmitate", "Sorbitan monopalmitat", "Emulsifikues", false, "Emulsifikues", "I sigurt")
    )
    
    /**
     * Normalize an E-code string: uppercase the prefix, keep letter suffix lowercase.
     * E.g. "e150A" -> "E150a", "E102" -> "E102"
     */
    private fun normalizeECode(code: String): String {
        val trimmed = code.trim()
        if (trimmed.length < 4) return trimmed.uppercase()
        val prefix = trimmed.takeWhile { it.isLetter() || it.isDigit() }
        val letterSuffix = trimmed.drop(prefix.length)
        // For codes like "E150a", the base part is "E150" and suffix is "a"
        val base = trimmed.take(4).uppercase() // "E150"
        val suffix = trimmed.drop(4).lowercase() // "a"
        return base + suffix
    }

    /**
     * Look up an E-code by its code
     */
    fun getECode(code: String): ECode? {
        return eCodes[normalizeECode(code)]
    }
    
    /**
     * Check if a code is an E-code
     */
    fun isECode(code: String): Boolean {
        val upper = code.trim().uppercase()
        return upper.startsWith("E") && upper.length in 4..6
    }
    
    /**
     * Extract all E-codes from an ingredients text
     */
    fun extractECodes(ingredients: String): List<ECode> {
        val eCodePattern = Regex("""E\d{3,4}[a-z]?""", RegexOption.IGNORE_CASE)
        val matches = eCodePattern.findAll(ingredients)
        return matches.mapNotNull { match ->
            getECode(match.value)
        }.distinct().toList()
    }
}
