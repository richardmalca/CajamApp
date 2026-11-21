package pe.cajamapp.ui.components

import androidx.annotation.DrawableRes
import pe.cajamapp.R

data class DestinationPhoto(
    val caption: String,
    val author: String,
    val source: String,
    val license: String? = null,
    @DrawableRes val drawable: Int? = null,
    val imageUrl: String? = null
)

fun destinationPhotos(id: Long): List<DestinationPhoto> = photoCatalog[id].orEmpty()

private val photoCatalog = mapOf(
    1L to listOf(
        DestinationPhoto(
            "Jardines y pozas de Baños del Inca", "José Manuel Pacuri Sullón",
            "https://commons.wikimedia.org/wiki/File:Aguas_termales_-_Ba%C3%B1os_del_Inca.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.banos_del_inca
        ),
        DestinationPhoto(
            "Aguas termales del complejo", "José Manuel Pacuri Sullón",
            "https://commons.wikimedia.org/wiki/File:Aguas_sulforicas_termales_-_Ba%C3%B1os_del_Inca.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.banos_pozas
        ),
    ),
    2L to listOf(
        DestinationPhoto(
            "Formaciones rocosas de Cumbemayo", "Tadd y Debbie Ottman",
            "https://commons.wikimedia.org/wiki/File:Cumbemayo_Cajamarca.jpg",
            "https://creativecommons.org/licenses/by/2.0/", drawable = R.drawable.cumbemayo
        ),
        DestinationPhoto(
            "Canal tallado en roca", "EnriqueGuia",
            "https://commons.wikimedia.org/wiki/File:Canal_Cumbemayo.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.cumbemayo_canal
        ),
    ),
    3L to listOf(
        DestinationPhoto(
            "Nichos del conjunto arqueológico", "Elisolidum",
            "https://commons.wikimedia.org/wiki/File:Las_Ventanillas_de_Otuzco.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.otuzco_nichos
        ),
        DestinationPhoto(
            "Vista panorámica de las Ventanillas de Otuzco", "Daniel Maza",
            "https://commons.wikimedia.org/wiki/File:Ventanillas_de_Otuzco.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.otuzco
        ),
    ),
    4L to listOf(
        DestinationPhoto(
            "Vista del Cuarto del Rescate", "Polmartin14+",
            "https://commons.wikimedia.org/wiki/File:Turistas_visitando_el_Cuarto_del_Rescate_-_Cajamarca.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.rescate_exterior
        ),
        DestinationPhoto(
            "Interior del Cuarto del Rescate", "Hellcome",
            "https://commons.wikimedia.org/wiki/File:Cajamarca_-_el_cuarto_del_rescate.jpg",
            "https://creativecommons.org/publicdomain/mark/1.0/", drawable = R.drawable.cuarto_rescate
        ),
    ),
    5L to listOf(
        DestinationPhoto(
            "Paisaje de Granja Porcón", "AgainErick",
            "https://commons.wikimedia.org/wiki/File:Granja_Porc%C3%B3n_-_View.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.porcon_bosque
        ),
        DestinationPhoto(
            "Vicuñas en Granja Porcón", "AgainErick",
            "https://commons.wikimedia.org/wiki/File:Granja_Porc%C3%B3n_-_View_with_vicu%C3%B1as.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.porcon_vicunas
        ),
    ),
    6L to listOf(
        DestinationPhoto(
            "Escalinata hacia la capilla de Fátima", "Pitxiquin",
            "https://commons.wikimedia.org/wiki/File:Escales_que_pugen_al_cerro_de_Santa_Apolonia_amb_la_capella_de_Nuestra_Se%C3%B1ora_de_F%C3%A1tima_a_dalt.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.apolonia_escaleras
        ),
        DestinationPhoto(
            "Vista de Cajamarca desde el cerro", "Pitxiquin",
            "https://commons.wikimedia.org/wiki/File:Vista_panor%C3%A0mica_de_Cajamarca_des_del_Cerro_de_Santa_Apolonia03.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.apolonia_mirador
        ),
    ),
    7L to listOf(
        DestinationPhoto(
            "Nichos funerarios de Combayo", "Gerhard Huber",
            "https://global-geography.org/af/Geography/America/Peru/Pictures/Cajamarca/Ventanillas_de_Combayo_4",
            "https://creativecommons.org/licenses/by-nc/4.0/", drawable = R.drawable.combayo_vista_4
        ),
        DestinationPhoto(
            "Pared rocosa de las Ventanillas de Combayo", "Gerhard Huber",
            "https://global-geography.org/af/Geography/America/Peru/Pictures/Cajamarca/Ventanillas_de_Combayo_3",
            "https://creativecommons.org/licenses/by-nc/4.0/", drawable = R.drawable.combayo_vista_3
        ),
    ),
    8L to listOf(
        DestinationPhoto(
            "Laguna San Nicolás, Namora", "cesquisa",
            "https://commons.wikimedia.org/wiki/File:Laguna_San_Nicol%C3%A1s_-_panoramio.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.namora_laguna
        ),
        DestinationPhoto(
            "Otra vista de la laguna San Nicolás", "cesquisa",
            "https://commons.wikimedia.org/wiki/File:Laguna_San_Nicol%C3%A1s_-_panoramio_(2).jpg",
            "https://creativecommons.org/licenses/by/3.0/", drawable = R.drawable.namora_orilla
        ),
    ),
    9L to listOf(
        DestinationPhoto(
            "Comunidad de Cumbico, Magdalena (entorno)",
            "Diario Grupo Panorama",
            "https://www.diariogrupopanorama.com/inician-construccion-de-qocha-agua-negra/",
            imageUrl = "https://www.diariogrupopanorama.com/wp-content/uploads/2025/11/2-4-e1762523866270.jpg"
        )
    ),
    10L to listOf(
        DestinationPhoto(
            "Panorámica del sitio arqueológico", "Pitxiquin",
            "https://commons.wikimedia.org/wiki/File:Panor%C3%A0mica_de_les_ru%C3%AFnes_de_Kuntur_Wasi.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.kuntur_ruinas
        ),
        DestinationPhoto(
            "Muros del complejo Kuntur Wasi", "Jorge.floresparedes",
            "https://commons.wikimedia.org/wiki/File:Muros_de_Kuntur_Wasi.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.kuntur_muros
        ),
    ),
    11L to listOf(
        DestinationPhoto(
            "Hacienda La Colpa", "Enrique Jara",
            "https://commons.wikimedia.org/wiki/File:HACIENDA_LA_COLPA_08.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.colpa_hacienda
        ),
        DestinationPhoto(
            "Vista de Hacienda La Colpa", "Enrique Jara",
            "https://commons.wikimedia.org/wiki/File:HACIENDA_LA_COLPA_01.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.colpa_capilla
        ),
    ),
    12L to listOf(
        DestinationPhoto(
            "Ruta hacia Pampa de la Culebra (entorno)", "Wikiloc · ruta Los Baños del Inca – Pampa de La Culebra",
            "https://es.wikiloc.com/rutas-mountain-bike/los-banos-del-inca-pampa-de-la-culebra-55189052",
            imageUrl = "https://s2.wklcdn.com/image_234/7034686/55189052/36886562.700x525.jpg"
        ),
    ),
    13L to listOf(
        DestinationPhoto(
            "Interior del Santuario de Polloc", "Agencia Info Salesiana",
            "https://www.infoans.org/sezioni/notizie/item/7960-peru-l-eredita-artistica-di-don-ugo-de-censi-continua-a-battere-in-ogni-angolo-del-paese",
            imageUrl = "https://www.infoans.org/images/05-Galleria-Fotografica/PER%20-%20Arte%20Polloc%202019/09-Per-02.jpg"
        ),
        DestinationPhoto(
            "Mosaicos del santuario", "Agencia Info Salesiana",
            "https://www.infoans.org/sezioni/notizie/item/7960-peru-l-eredita-artistica-di-don-ugo-de-censi-continua-a-battere-in-ogni-angolo-del-paese",
            imageUrl = "https://www.infoans.org/media/k2/items/cache/08ed795e75a158f51e8e490e0a789dbf_XL.jpg"
        ),
    ),
    14L to listOf(
        DestinationPhoto(
            "Bosques del Parque Nacional Cutervo", "Michael O. Dillon",
            "https://commons.wikimedia.org/wiki/File:Forests_at_Cutervo_National_Park.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.cutervo_bosque
        ),
        DestinationPhoto(
            "Relieve del parque y cerro Pabellón", "User:Braxdocument",
            "https://commons.wikimedia.org/wiki/File:Relieve_callayujano%2C_al_fondo_el_dominante_cerro_Pabell%C3%B3n..jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.cutervo_paisaje
        ),
        DestinationPhoto(
            "Riachuelo del Parque Nacional Cutervo", "Manuelriverscix",
            "https://commons.wikimedia.org/wiki/File:Riachuelo_del_Parque_Nacional_de_Cutervo.jpg",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.cutervo_riachuelo
        ),
    ),
    15L to listOf(
        DestinationPhoto(
            "Soldados de piedra de Huangashanga, Huasmín", "Destinos Peruanos",
            "https://destinosperuanos.wordpress.com/2014/12/15/huasmin/",
            imageUrl = "https://destinosperuanos.wordpress.com/wp-content/uploads/2014/12/88189037.jpg"
        ),
    ),
    16L to listOf(
        DestinationPhoto(
            "Complejo termal de Chancaybaños", "Inventario Nacional de Recursos Turísticos · MINCETUR",
            "https://consultasenlinea.mincetur.gob.pe/fichaInventario/index.aspx?cod_Ficha=2146",
            imageUrl = "https://consultasenlinea.mincetur.gob.pe/fichaInventario/foto.aspx?cod=625611"
        ),
        DestinationPhoto(
            "Piscinas del complejo termal", "Inventario Nacional de Recursos Turísticos · MINCETUR",
            "https://consultasenlinea.mincetur.gob.pe/fichaInventario/index.aspx?cod_Ficha=2146",
            imageUrl = "https://consultasenlinea.mincetur.gob.pe/fichaInventario/foto.aspx?cod=625614"
        ),
    ),
    17L to listOf(
        DestinationPhoto(
            "Cerro Condorcaga, Chuyabamba (entorno)", "Gobierno Regional de Cajamarca",
            "https://destino.regioncajamarca.gob.pe/prv/0604",
            imageUrl = "https://destino.regioncajamarca.gob.pe/media/provincias/0604/060410/atractivos/Cerro_Condorcaga_Chuyabamba.JPG"
        ),
    ),
    18L to listOf(
        DestinationPhoto(
            "Conjunto Monumental Belén", "Catatine",
            "https://commons.wikimedia.org/wiki/File:Cajamarca_ConjuntoMonumental1.JPG",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.belen_conjunto
        ),
        DestinationPhoto(
            "Antiguo hospital de mujeres de Belén", "Catatine",
            "https://commons.wikimedia.org/wiki/File:Cajamarca_Bel%C3%A9n_HospitalMujeres.JPG",
            "https://creativecommons.org/licenses/by-sa/4.0/", drawable = R.drawable.belen_hospital
        ),
        DestinationPhoto(
            "Plaza de Armas de Cajamarca", "Calebperu",
            "https://commons.wikimedia.org/wiki/File:Cajamarca.JPG",
            "https://creativecommons.org/licenses/by/3.0/", drawable = R.drawable.plaza_cajamarca
        ),
    ),
)
