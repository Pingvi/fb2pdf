<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/main.css"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Конвертор FictionBook2 в PDF для Sony Reader</title>

<script type="text/javascript">
function get(id)
{
    return document.getElementById(id);
}

//  Show/Hide 5 first titles
function bookTitles(divName,imgName)
{

    if ( get(divName).style.display == 'none') 
    {
        get(divName).style.display="inline";
        get(imgName).src = get(imgName).src.replace('_plus', '_minus');
    }   
    else
    {
        get(divName).style.display="none";
        get(imgName).src = get(imgName).src.replace('_minus', '_plus');
    }
}

function rollOver(imgName)
{
    if ( get(imgName).src.search('_off') > 0 )
    {
        get(imgName).src = get(imgName).src.replace('_off', '_on');
    }
    else
    {
        get(imgName).src = get(imgName).src.replace('_on', '_off');
    }
}

</script>
</head>
<body>

<center>
<div id="container" class="WidthPage">
    <?php 
    include 'header.inc.php'; 
    $active_menu = 'library';
    include 'menu.inc.php'; 
    ?>
    
    <div id="tab_box">
        <b class="xtop"><b class="xb1"></b><b class="xb2"></b><b class="xb3"></b><b class="xb4"></b></b>
        <div class="tab_box_content">
            <!--  display alphabet --> 
            <div id="alphabet">
                <?php
                require_once 'awscfg.php';
                require_once 'db.php';
                require_once 'utils.php';

                global $dbServer, $dbName, $dbUser, $dbPassword;

                $db = new DB($dbServer, $dbName, $dbUser, $dbPassword);
                $dbLetters = $db->getAuthorsFirstLetters();

                $letter = isset($_GET["letter"]) ? strtoupper($_GET["letter"]) : "А";
                $alphabet = array(
                    array("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"),
                    array("А","Б","В","Г","Д","Е","Ё","Ж","З","И","Й","К","Л","М","Н","О","П","Р","С","Т","У","Ф","Х","Ц","Ч","Ш","Щ","Э","Ю","Я")
                    );

                for ($i = 0; $i < count($alphabet); $i++)
                {
                    $letters = $alphabet[$i];
                    foreach ($letters as $l)
                    {
                        if (in_array($l, $dbLetters))
                        {
                            if ($letter == $l)
                                echo "<span class=\"active\">$l</span> ";
                            else
                                echo "<a href=\"library.php?letter=$l\">$l</a> ";
                        }
                        else
                            echo "$l ";
                    }
                    echo "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                }
                ?>
            </div>
            <img src="images/green_px.gif" class="line"/>
            
            <!--  display authors --> 
            <?php
            $list = $db->getAuthorsByFirstLetter($letter);
            if ($list)
            {
                $count = count($list);
                
                $MAX_COLS  = 2;
                $MAX_ROWS  = ($count + $MAX_COLS - 1) / $MAX_COLS;
                
                for ($col = $MAX_COLS - 1; $col >= 0 ; $col--)
                {
                    if ($col == 0)
                        echo '<div class="left_book">';
                    else
                        echo '<div class="right_book">';
                    
                    for ($row = 0; $row < $MAX_ROWS; $row++)
                    {
                        $i = $col * $MAX_ROWS + $row;
                        echo "<p>";
                        if ($i < $count)
                        {
                            $author = $list[$i]["author"];
                            $number = $list[$i]["number"];
                            
                            echo "<img id=\"bt$author\" src=\"images/bt_plus_off.gif\" alt=\"plus\"";
                            echo " onclick=\"bookTitles('$author', 'bt$author');\"";
                            echo " onmouseover=\"rollOver('bt$author');\"";
                            echo " onmouseout=\"rollOver('bt$author');\"/>";
                            echo "&nbsp;&nbsp;$author&nbsp;&nbsp;<br/>"; 
                        }
                        echo "</p>";
                    }
                    echo '</div>';
                }
            }
            ?>
            
            <img src="images/green_px.gif" class="line"/>
            <?php include 'footer.inc.php'; ?>
        </div>  <!--end of tab box content-->	
        <b class="xbottom"><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
    </div> <!--end of tab box -->
<br/>
<br/>
</div> <!--end of container-->
</center>
</body>
</html>